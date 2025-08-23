package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription

abstract class PassiveEventStreamVertex<EventT> : EventStreamVertex<EventT>() {
    private var subscription: Subscription? = null

    final override fun onResumed() {
        startObserving()
    }

    final override fun onPaused() {
        stopObserving()
    }

    private fun startObserving() {
        if (subscription != null) {
            throw AssertionError("The passive event stream vertex is already observing its source(s)")
        }

        subscription = observe()
    }

    private fun stopObserving() {
        val subscription =
            this.subscription ?: throw AssertionError("The passive event stream vertex is not observing its source(s)")

        subscription.cancel()

        this@PassiveEventStreamVertex.subscription = null
    }

    /**
     * Start observing the source(s) of this vertex and return a [Subscription] that can be used to stop observing them.
     *
     * This method is called during the mutation phase.
     */
    protected abstract fun observe(): Subscription
}
