package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class DependentEventStream<E> : ManagedEventStream<E>() {
    private var dependentSubscription: Subscription? = null

    final override fun onResumed() {
        if (dependentSubscription != null) {
            throw AssertionError("The subscription is already present")
        }

        dependentSubscription = observe()
    }

    final override fun onPaused() {
        val subscription = this.dependentSubscription ?: throw AssertionError("There's no subscription")

        subscription.cancel()
        this@DependentEventStream.dependentSubscription = null
    }

    protected abstract fun observe(): Subscription?
}
