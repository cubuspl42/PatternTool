package diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.ilde_focus_states

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.EventStreamSlot
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.Triggers
import diy.lingerie.cup_layout_tool.UserBezierMesh
import diy.lingerie.cup_layout_tool.application_state.interaction_state.ManipulationState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.HandleDragState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.IdleFocusState

class FocusedHandleState(
    val focusedHandle: UserBezierMesh.Handle,
    val doDragSlot: EventStreamSlot<DragCommand>,
) : IdleFocusState() {
    data class DragCommand(
        val targetPosition: Cell<Point?>,
        val doCommit: EventStream<Unit>,
    )

    companion object {
        fun enter(
            focusedHandle: UserBezierMesh.Handle,
        ): Effect<FocusedHandleState> = Effect.prepared {
            val doDragSlot = EventStreamSlot.create<DragCommand>()

            Effect.pureTriggering(
                result = FocusedHandleState(
                    focusedHandle = focusedHandle,
                    doDragSlot = doDragSlot,
                ),
                trigger = Triggers.Noop,
            )
        }
    }

    override val doChangeOuterState: EventStream<Effect<ManipulationState>>
        get() = doDragSlot.mapAt { dragCommand ->
            HandleDragState.enter(
                originalPosition = focusedHandle.position.sample(),
                dragCommand = dragCommand,
                draggedHandle = focusedHandle,
            )
        }
}
