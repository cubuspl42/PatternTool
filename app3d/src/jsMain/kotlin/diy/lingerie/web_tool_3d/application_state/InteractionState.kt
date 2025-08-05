package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.future.Future
import diy.lingerie.web_tool_3d.UserBezierMesh

class InteractionState(
    private val documentState: DocumentState,
) {
    sealed class PrimaryInteraction

    class HandleFocusInteraction(
        val focusedHandle: UserBezierMesh.Handle,
    ) : PrimaryInteraction()

    class HandleDragInteraction(
        val draggedHandle: UserBezierMesh.Handle,
        val requestedHandlePosition: Cell<Point>,
    ) : PrimaryInteraction()

    private val mutableOngoingPrimaryInteraction: MutableCell<PrimaryInteraction?> = MutableCell(
        initialValue = null,
    )

    val ongoingPrimaryInteraction: Cell<PrimaryInteraction?>
        get() = mutableOngoingPrimaryInteraction

    fun startHandleFocusInteraction(
        handle: UserBezierMesh.Handle,
        until: Future<Unit>,
    ) {

    }

    fun startHandleDragInteraction(
        handle: UserBezierMesh.Handle,
        requestedHandlePosition: Cell<Point>,
        until: Future<Unit>,
    ): HandleDragInteraction? {
        if (mutableOngoingPrimaryInteraction.currentValue != null) return null

        val newHandleDragInteraction = HandleDragInteraction(
            draggedHandle = handle,
            requestedHandlePosition = requestedHandlePosition,
        )

        mutableOngoingPrimaryInteraction.set(newHandleDragInteraction)

        until.onFulfilled.forEach {
            mutableOngoingPrimaryInteraction.set(null)
        }

        handle.position.bindUntil(
            boundValue = requestedHandlePosition,
            until = until,
        )

        return newHandleDragInteraction
    }
}
