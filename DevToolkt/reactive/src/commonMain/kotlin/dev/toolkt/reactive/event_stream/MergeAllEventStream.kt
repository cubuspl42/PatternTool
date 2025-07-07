package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

class MergeAllEventStream<E>(
    private val sources: List<EventStream<E>>,
) : DependentEventStream<E>() {
    override fun observe(): Subscription = object : Subscription, Listener<E> {
        private val subscriptions = sources.map {
            it.listen(listener = this)
        }

        override fun cancel() {
            subscriptions.forEach {
                it.cancel()
            }
        }

        override fun handle(event: E) {
            notify(event = event)
        }
    }
}
