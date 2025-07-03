package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class StatefulEventStream<E>() : DependentEventStream<E>() {
    final override fun observe(): Subscription = observeStateful()

    abstract fun observeStateful(): Subscription
}
