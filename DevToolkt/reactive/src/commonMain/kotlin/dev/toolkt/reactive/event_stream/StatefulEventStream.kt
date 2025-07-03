package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class StatefulEventStream<E>() : DependentEventStream<E>() {
    final override fun observe(): Subscription = observeStateful()

    abstract fun observeStateful(): Subscription

    protected fun init() {
        pinWeak(target = this)
    }
}

abstract class HybridEventStream<E>() : ManagedEventStream<E>() {

    override fun onResumed() {
        TODO("Not yet implemented")
    }

    override fun onPaused() {
        TODO("Not yet implemented")
    }
}
