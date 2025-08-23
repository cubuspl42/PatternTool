package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream

internal class StaticMergeAllEventStreamVertex<EventT>(
    private val sources: List<EventStream<EventT>>,
) : PassiveEventStreamVertex<EventT>() {
    override fun observe(): Subscription = object : UnconditionalListener<EventT>(), Subscription {
        private val subscriptions = sources.map { eventStream ->
            eventStream.listen(listener = this)
        }

        override fun cancel() {
            subscriptions.forEach { subscription ->
                subscription.cancel()
            }
        }

        override fun handleUnconditionally(
            transaction: Transaction,
            event: EventT,
        ) {
            notify(
                transaction = transaction,
                event = event,
            )
        }
    }
}
