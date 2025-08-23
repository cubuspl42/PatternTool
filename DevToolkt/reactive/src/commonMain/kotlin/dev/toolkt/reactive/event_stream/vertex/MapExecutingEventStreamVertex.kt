package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.VertexEventStream

internal class MapExecutingEventStreamVertex<TransformedEventT> private constructor() :
    ActiveEventStreamVertex<TransformedEventT>() {

    companion object {
        fun <EventT, TransformedEventT> start(
            transaction: Transaction,
            source: EventStream<EventT>,
            transform: context(ActionContext) (EventT) -> TransformedEventT,
        ): Pair<MapExecutingEventStreamVertex<TransformedEventT>, Subscription> {
            source as VertexEventStream<EventT> // FIXME: Remove this cast

            return MapExecutingEventStreamVertex<TransformedEventT>().let { self ->
                val subscription = source.vertex.subscribeLater(
                    transaction = transaction,
                    listener = object : UnconditionalListener<EventT>() {
                        override fun handleUnconditionally(
                            transaction: Transaction,
                            event: EventT,
                        ) {
                            val transformedEvent = with(transaction) {
                                transform(event)
                            }

                            self.notify(
                                transaction = transaction,
                                event = transformedEvent,
                            )
                        }
                    },
                )

                Pair(self, subscription)
            }
        }
    }
}
