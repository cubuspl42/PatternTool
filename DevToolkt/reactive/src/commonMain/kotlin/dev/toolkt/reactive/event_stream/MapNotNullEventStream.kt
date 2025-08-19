package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Transaction

internal class MapNotNullEventStream<EventT, TransformedEventT : Any>(
    source: EventStream<EventT>,
    private val transform: (EventT) -> TransformedEventT?,
) : TransformingEventStream<EventT, TransformedEventT>(
    source = source,
) {
    override fun transformEvent(
        transaction: Transaction,
        event: EventT,
    ) {
        val transformedEvent = transform(event) ?: return

        notify(
            transaction = transaction,
            event = transformedEvent,
        )
    }
}
