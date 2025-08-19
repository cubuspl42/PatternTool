package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.managed_io.Transaction

internal class StaticMergeAllEventStream<EventT>(
    private val sources: List<EventStream<EventT>>,
) : DependentEventStream<EventT>() {
    override fun observe(): Subscription = object : UnconditionalListener<EventT>(), Subscription {
        private val subscriptions = sources.map {
            it.listen(listener = this)
        }

        override fun cancel() {
            subscriptions.forEach {
                it.cancel()
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
