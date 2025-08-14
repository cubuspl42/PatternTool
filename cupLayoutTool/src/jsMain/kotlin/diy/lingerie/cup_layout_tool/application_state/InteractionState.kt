package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.connect
import dev.toolkt.reactive.event_stream.EventStream
import diy.lingerie.cup_layout_tool.UserBezierMesh

class InteractionState(
    private val documentState: DocumentState,
) {
    sealed class ManipulationState {
        abstract fun leave()
    }

    sealed class IdleFocusRequest

    class HandleFocusRequest(
        val indicatedHandle: UserBezierMesh.Handle,
    ) : IdleFocusRequest()

    class UserBezierMeshFocusRequest(
        val indicatedMesh: UserBezierMesh,
    ) : IdleFocusRequest()

    sealed class IdleFocusState {
        protected abstract val idleState: IdleState

        protected fun isActive(): Boolean = idleState.isActive() && idleState.currentFocusState == this
    }

    class FocusedHandleState(
        override val idleState: IdleState,
        val focusedHandle: UserBezierMesh.Handle,
    ) : IdleFocusState() {
        private val interactionState: InteractionState
            get() = idleState.interactionState

        fun drag(
            targetPosition: Cell<Point?>,
            doCommit: EventStream<Unit>,
        ) {
            require(isActive()) { "drag(): !isActive()" }

            interactionState.enterManipulationState(
                HandleDragState.setup(
                    interactionState = interactionState,
                    originalPosition = focusedHandle.position.currentValue,
                    draggedHandle = focusedHandle,
                    targetPosition = targetPosition,
                    doCommit = doCommit,
                ),
            )
        }
    }

    class FocusedUserBezierMeshState(
        override val idleState: IdleState,
        val focusedMesh: UserBezierMesh,
    ) : IdleFocusState() {
        fun select() {
            require(isActive()) { "select(): !isActive()" }
        }
    }

    class IdleState(
        internal val interactionState: InteractionState,
    ) : ManipulationState() {
        internal fun isActive(): Boolean = interactionState.currentManipulationState == this

        private val mutableFocusState = MutableCell<IdleFocusState?>(initialValue = null)

        fun clearFocus() {
            mutableFocusState.set(null)
        }

        fun focusHandle(
            handle: UserBezierMesh.Handle,
        ) {
            mutableFocusState.set(
                FocusedHandleState(
                    idleState = this,
                    focusedHandle = handle,
                )
            )
        }

        val focusState: MutableCell<IdleFocusState?>
            get() = mutableFocusState

        val focusedHandle: Cell<UserBezierMesh.Handle?>
            get() = focusState.map {
                val focusedHandleState = it as? FocusedHandleState ?: return@map null

                focusedHandleState.focusedHandle
            }

        val currentFocusState: IdleFocusState?
            get() = focusState.currentValue

        override fun leave() {
        }
    }

    class HandleDragState private constructor(
        private val interactionState: InteractionState,
        private val originalPosition: Point,
        val draggedHandle: UserBezierMesh.Handle,
    ) : ManipulationState() {
        companion object {
            fun setup(
                interactionState: InteractionState,
                originalPosition: Point,
                targetPosition: Cell<Point?>,
                doCommit: EventStream<Unit>,
                draggedHandle: UserBezierMesh.Handle,
            ): HandleDragState {
                val handleDragState = HandleDragState(
                    interactionState = interactionState,
                    originalPosition = originalPosition,
                    draggedHandle = draggedHandle,
                )

                val effectiveTargetPosition = targetPosition.map { it ?: originalPosition }

                effectiveTargetPosition.connect(
                    targetCell = draggedHandle.position,
                    doDisconnect = doCommit,
                )

                doCommit.single().forEach {
                    interactionState.enterIdleState()
                }

                return handleDragState
            }
        }

        private fun isActive(): Boolean = interactionState.currentManipulationState == this


        fun abort() {
            require(isActive()) { "abort(): !isActive()" }

            draggedHandle.position.set(originalPosition)

            interactionState.enterIdleState()
        }

        override fun leave() {
        }
    }

    private val mutableManipulationState: MutableCell<ManipulationState> = MutableCell(
        initialValue = IdleState(interactionState = this),
    )

    val manipulationState: Cell<ManipulationState>
        get() = mutableManipulationState

    val idleFocusState: Cell<IdleFocusState?>
        get() = manipulationState.switchOf {

            val idleState = it as? IdleState ?: return@switchOf Cell.of(null)
            idleState.focusState
        }

    val focusedHandle: Cell<UserBezierMesh.Handle?>
        get() = manipulationState.switchOf {
            val idleState = it as? IdleState ?: return@switchOf Cell.of(null)

            idleState.focusedHandle
        }

    val currentManipulationState: ManipulationState
        get() = manipulationState.currentValue

    internal fun enterManipulationState(
        newManipulationState: ManipulationState,
    ) {
        val previousState = mutableManipulationState.currentValue

        previousState.leave()

        mutableManipulationState.set(newManipulationState)
    }

    private fun enterIdleState() {
        enterManipulationState(
            IdleState(interactionState = this),
        )
    }
}
