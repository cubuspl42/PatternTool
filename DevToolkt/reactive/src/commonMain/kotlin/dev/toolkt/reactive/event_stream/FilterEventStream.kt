package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.Transaction

internal class FilterEventStream<EventT> private constructor(
    source: EventStream<EventT>,
    private val predicate: (EventT) -> Boolean,
) : TransformingEventStream<EventT, EventT>(
    source = source,
) {
    companion object {
        fun <EventT> construct(
            source: EventStream<EventT>,
            predicate: (EventT) -> Boolean,
        ): FilterEventStream<EventT> = FilterEventStream(
            source = source,
            predicate = predicate,
        )
    }

    override fun transformEvent(transaction: Transaction, event: EventT) {
        if (predicate(event)) {
            notify(
                transaction = transaction,
                event = event,
            )
        }
    }
}
