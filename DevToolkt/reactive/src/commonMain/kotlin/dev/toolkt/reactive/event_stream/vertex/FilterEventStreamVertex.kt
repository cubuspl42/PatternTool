package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream

class FilterEventStreamVertex<EventT>(
    source: EventStream<EventT>,
    private val predicate: context(MomentContext) (EventT) -> Boolean,
) : TransformingEventStreamVertex<EventT, EventT>(
    source = source,
) {
    override fun transformEvent(
        transaction: Transaction,
        event: EventT,
    ): EventTransformation<EventT>? {
        val shouldForward = with(transaction) {
            predicate(event)
        }

        return when {
            shouldForward -> EventTransformation(event)
            else -> null
        }
    }
}
