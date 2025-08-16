package diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.CellSlot
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.actuateOf
import dev.toolkt.reactive.managed_io.map
import diy.lingerie.cup_layout_tool.UserBezierMesh
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.ilde_focus_states.FocusedHandleState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.ManipulationState

class IdleState private constructor(
    val indicatedObject: CellSlot<IndicatedObject?>,
    val focusState: Cell<IdleFocusState?>,
) : ManipulationState() {
    sealed class IndicatedObject
    data class HandleIndicatedObject(
        val handle: UserBezierMesh.Handle,
    ) : IndicatedObject()

    data class MeshIndicatedObject(
        val mesh: UserBezierMesh,
    ) : IndicatedObject()

    companion object {
        fun enter(): Effect<IdleState> = Effect.prepared {
            val indicatedObject = CellSlot.create<IndicatedObject?>()

            indicatedObject.actuateOf { indicatedObject ->
                when (indicatedObject) {
                    is HandleIndicatedObject -> FocusedHandleState.enter(
                        focusedHandle = indicatedObject.handle,
                    )

                    else -> Effect.Null
                }
            }.map { focusState ->
                IdleState(
                    indicatedObject = indicatedObject,
                    focusState = focusState,
                )
            }
        }
    }

    val focusedHandle: Cell<UserBezierMesh.Handle?>
        get() = focusState.map {
            val focusedHandleState = it as? FocusedHandleState ?: return@map null

            focusedHandleState.focusedHandle
        }

    override val doChangeState: EventStream<Effect<ManipulationState>>
        get() = focusState.divertOf {
            it?.doChangeOuterState ?: EventStream.Never
        }
}
