package diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.forwardToUntil
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.EventStreamSlot
import dev.toolkt.reactive.event_stream.forEach
import dev.toolkt.reactive.event_stream.mergeWith
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.Triggers
import diy.lingerie.cup_layout_tool.UserBezierMesh
import diy.lingerie.cup_layout_tool.application_state.interaction_state.ManipulationState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.IdleState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.ilde_focus_states.FocusedHandleState

class HandleDragState private constructor(
    val doAbortSlot: EventStreamSlot<Unit>,
    private val doCommit: EventStream<Unit>,
) : ManipulationState() {
    companion object {
        fun enter(
            originalPosition: Point,
            dragCommand: FocusedHandleState.DragCommand,
            draggedHandle: UserBezierMesh.Handle,
        ): Effect<HandleDragState> {
            val doCommit = dragCommand.doCommit

            val effectiveTargetPosition = dragCommand.targetPosition.map {
                it ?: originalPosition
            }

            return Effect.prepared {
                val doAbortSlot = EventStreamSlot.create<Unit>()

                Effect.pureTriggering(
                    result = HandleDragState(
                        doAbortSlot = doAbortSlot,
                        doCommit = doCommit,
                    ),
                    trigger = Triggers.combine(
                        doAbortSlot.forEach {
                            draggedHandle.position.set(originalPosition)
                        },
                        effectiveTargetPosition.forwardToUntil(
                            targetCell = draggedHandle.position,
                            // Is it needed?
                            doDisconnect = doCommit,
                        ),
                    ),
                )
            }
        }
    }

    private val doLeave: EventStream<Unit>
        get() = doCommit.mergeWith(doAbortSlot)

    override val doChangeState: EventStream<Effect<ManipulationState>>
        get() = doLeave.map {
            IdleState.enter()
        }
}
