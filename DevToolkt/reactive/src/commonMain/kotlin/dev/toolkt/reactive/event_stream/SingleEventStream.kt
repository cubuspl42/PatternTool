package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

class SingleEventStream<out E>(
    source: EventStream<E>,
) : StatefulEventStream<E>() {
    private var wasEmitted = false

    private var sourceSubscription: Subscription? = source.listenWeak(
        target = this,
    ) { self, sourceEvent ->
        if (self.wasEmitted) {
            throw IllegalStateException("The single event was already emitted")
        }

        self.notify(event = sourceEvent)

        self.wasEmitted = true

        val sourceSubscription = self.sourceSubscription ?: throw IllegalStateException("No active source subscription")

        sourceSubscription.cancel()

        self.sourceSubscription = null
    }
}
