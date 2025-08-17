package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction

internal class MapAtEventStream<EventT, TransformedEventT>(
    source: EventStream<EventT>,
    private val transform: context(MomentContext) (EventT) -> TransformedEventT,
) : TransformingEventStream<EventT, TransformedEventT>(
    source = source,
) {
    override fun transformEvent(
        transaction: Transaction,
        event: EventT,
    ) {
        TODO()
    }
}
