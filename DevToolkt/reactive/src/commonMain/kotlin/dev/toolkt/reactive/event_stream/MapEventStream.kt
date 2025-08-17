package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Transaction

internal class MapEventStream<E, Er>(
    source: EventStream<E>,
    private val transform: (E) -> Er,
) : TransformingEventStream<E, Er>(
    source = source,
) {
    override fun transformEvent(
        transaction: Transaction,
        event: E,
    ) {
        notify(
            transaction = transaction,
            event = transform(event),
        )
    }
}
