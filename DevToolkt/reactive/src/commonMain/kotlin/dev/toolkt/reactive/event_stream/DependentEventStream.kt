package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

internal abstract class DependentEventStream<EventT> : ManagedEventStream<EventT>() {
    private var subscription: Subscription? = null

    final override fun onResumed() {
        startObserving()
    }

    final override fun onPaused() {
        stopObserving()
    }

    final override fun onAborted() {
        stopObserving()
    }

    private fun startObserving() {
        if (subscription != null) {
            throw AssertionError("The subscription is already present")
        }

        subscription = observe()
    }

    private fun stopObserving() {
        val subscription = this.subscription ?: throw AssertionError("There's no subscription")

        subscription.cancel()
        this@DependentEventStream.subscription = null
    }

    protected abstract fun observe(): Subscription
}
