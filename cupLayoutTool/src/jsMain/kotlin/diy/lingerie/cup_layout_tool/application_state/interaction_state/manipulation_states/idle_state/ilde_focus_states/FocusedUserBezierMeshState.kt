package diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.ilde_focus_states

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.EventStreamSlot
import dev.toolkt.reactive.effect.Effect
import diy.lingerie.cup_layout_tool.UserBezierMesh
import diy.lingerie.cup_layout_tool.application_state.interaction_state.ManipulationState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.IdleFocusState

class FocusedUserBezierMeshState(
    val focusedMesh: UserBezierMesh,
    val doSelectSlot: EventStreamSlot<Unit>,
) : IdleFocusState() {
    companion object {
        fun enter(
            focusedMesh: UserBezierMesh,
        ): Effect<FocusedUserBezierMeshState> = Effect.prepared {
            val doSelectSlot = EventStreamSlot.create<Unit>()

            Effect.pure(
                FocusedUserBezierMeshState(
                    doSelectSlot = doSelectSlot,
                    focusedMesh = focusedMesh,
                ),
            )
        }
    }

    override val doChangeOuterState: EventStream<Effect<ManipulationState>>
        get() = EventStream.Never
}
