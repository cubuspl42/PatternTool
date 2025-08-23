package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream

class MapNotNullEventStreamVertex<EventT, TransformedEventT : Any>(
    source: EventStream<EventT>,
    private val transform: context(MomentContext) (EventT) -> TransformedEventT?,
) : TransformingEventStreamVertex<EventT, TransformedEventT>(
    source = source,
) {
    override fun transformEvent(
        transaction: Transaction,
        event: EventT,
    ): EventTransformation<TransformedEventT>? {
        val transformedEvent = with(transaction) {
            transform(event)
        } ?: return null

        return EventTransformation(
            transformedEvent = transformedEvent,
        )
    }
}
