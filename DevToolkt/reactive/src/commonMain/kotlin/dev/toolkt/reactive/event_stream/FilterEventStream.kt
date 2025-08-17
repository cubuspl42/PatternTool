package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Transaction

class FilterEventStream<E>(
    source: EventStream<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStream<E, E>(
    source = source,
) {
    override fun transformEvent(transaction: Transaction, event: E) {
        if (predicate(event)) {
            notify(
                transaction = transaction,
                event = event,
            )
        }
    }
}
