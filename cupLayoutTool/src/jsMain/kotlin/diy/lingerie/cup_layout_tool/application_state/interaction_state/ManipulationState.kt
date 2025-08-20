package diy.lingerie.cup_layout_tool.application_state.interaction_state

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.effect.Effect

abstract class ManipulationState {
    internal abstract val doChangeState: EventStream<Effect<ManipulationState>>
}
