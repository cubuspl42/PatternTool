package diy.lingerie.reactive.vertices.event_stream

import diy.lingerie.reactive.Subscription
import diy.lingerie.reactive.vertices.Vertex

abstract class EventStreamVertex<E> : Vertex<E>() {
    private var subscription: Subscription? = null

    final override fun onResumed() {
        if (subscription != null) {
            throw AssertionError("The stream $tag is already resumed (???)")
        }

        subscription = observe()
    }

    final override fun onPaused() {
        val sourceSubscription =
            this.subscription ?: throw AssertionError("The stream $tag is already paused (???)")

        sourceSubscription.cancel()
        subscription = null
    }

    protected abstract fun observe(): Subscription
}
