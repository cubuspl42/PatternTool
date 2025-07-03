package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

class SingleEventStream<E>(
    private val source: EventStream<E>,
) : StatefulEventStream<E>() {
    private var wasEmitted = false

    override fun observeStateful(): Subscription = source.listen { sourceEvent ->
        if (this.wasEmitted) {
            throw IllegalStateException("The single event was already emitted")
        }

        this.notify(event = sourceEvent)

        abort()
    }

    init {
        init()
    }
}
