package diy.lingerie.reactive.vertices.event_stream

import diy.lingerie.reactive.Listener
import diy.lingerie.reactive.Subscription
import diy.lingerie.reactive.vertices.Vertex

abstract class TransformingEventStreamVertex<E, Er>(
    private val source: Vertex<E>,
) : EventStreamVertex<Er>() {
    protected abstract fun handleSourceEvent(event: E)

    override fun observe(): Subscription = source.subscribeStrong(
        listener = object : Listener<E> {
            override fun handle(event: E) {
                handleSourceEvent(event)
            }
        },
    )
}
