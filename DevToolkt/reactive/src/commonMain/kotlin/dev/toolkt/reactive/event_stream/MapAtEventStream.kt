package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction

internal class MapAtEventStream<EventT, TransformedEventT> private constructor(
    source: EventStream<EventT>,
    private val transform: context(MomentContext) (EventT) -> TransformedEventT,
) : TransformingEventStream<EventT, TransformedEventT>(
    source = source,
) {
    companion object {
        fun <EventT, TransformedEventT> construct(
            source: EventStream<EventT>,
            transform: context(MomentContext) (EventT) -> TransformedEventT,
        ): MapAtEventStream<EventT, TransformedEventT> = MapAtEventStream(
            source = source,
            transform = transform,
        )
    }

    override fun transformEvent(
        transaction: Transaction,
        event: EventT,
    ) {
        val transformedEvent = with(transaction) {
            transform(event)
        }

        notify(
            transaction = transaction,
            event = transformedEvent,
        )
    }
}
