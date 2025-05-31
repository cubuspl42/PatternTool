package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class TransformingEventStream<E, Er>(
    private val source: EventStream<E>,
) : DependentEventStream<Er>() {
    final override fun observe(): Subscription? = source.subscribeStrong(
        eventHandler = object : EventHandler<E> {
            override fun handleEvent(source: EventSource<E>, event: E) {
                transformEvent(event)
            }

            override fun handleStop(source: EventSource<E>) {
                // TODO: Notify about the stop
            }
        },
    )

    protected abstract fun transformEvent(
        event: E,
    )

    final override val successorEventStream: EventStream<Er>?
        get() = null // This doesn't sound right
}
