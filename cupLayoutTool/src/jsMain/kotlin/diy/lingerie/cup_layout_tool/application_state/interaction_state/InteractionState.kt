package diy.lingerie.cup_layout_tool.application_state.interaction_state

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.map
import diy.lingerie.cup_layout_tool.UserBezierMesh
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.IdleState

class InteractionState(
    val manipulationState: Cell<ManipulationState>,
) {
    companion object {
        fun enter(): Effect<InteractionState> = Effect.prepared {
            Effect.deflectEffectively<ManipulationState>(
                initialValueEffect = IdleState.enter(),
                selectNextValueEffectFuture = { state -> state.doChangeState.next() },
            ).map { manipulationState ->
                InteractionState(
                    manipulationState = manipulationState,
                )
            }
        }
    }

    val focusedHandle: Cell<UserBezierMesh.Handle?>
        get() = manipulationState.switchOf {
            val idleState = it as? IdleState ?: return@switchOf Cell.of(null)

            idleState.focusedHandle
        }
}
