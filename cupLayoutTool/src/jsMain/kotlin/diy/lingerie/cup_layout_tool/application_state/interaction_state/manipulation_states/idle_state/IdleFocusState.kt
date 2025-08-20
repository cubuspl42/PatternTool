package diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.effect.Effect
import diy.lingerie.cup_layout_tool.application_state.interaction_state.ManipulationState

abstract class IdleFocusState {
    abstract val doChangeOuterState: EventStream<Effect<ManipulationState>>
}
