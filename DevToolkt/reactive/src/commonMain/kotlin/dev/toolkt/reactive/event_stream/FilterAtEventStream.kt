package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction

class FilterAtEventStream<EventT> private constructor(
    source: EventStream<EventT>,
    private val predicate: context(MomentContext) (EventT) -> Boolean,
) : TransformingEventStream<EventT, EventT>(
    source = source,
) {
    companion object {
        fun <EventT> construct(
            source: EventStream<EventT>,
            predicate: context(MomentContext) (EventT) -> Boolean,
        ): FilterAtEventStream<EventT> = FilterAtEventStream(
            source = source,
            predicate = predicate,
        )
    }

    override fun transformEvent(transaction: Transaction, event: EventT) {
        val shouldNotify = with(transaction) {
            predicate(event)
        }

        if (shouldNotify) {
            notify(
                transaction = transaction,
                event = event,
            )
        }
    }
}
