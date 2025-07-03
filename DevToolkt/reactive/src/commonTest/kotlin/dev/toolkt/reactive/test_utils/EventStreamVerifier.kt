package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.listenWeak

/**
 * A verifier for an [EventStream] that collects all received events through a weak listener.
 *
 * @constructor
 * @param eventStream the [EventStream] to verify. No strong reference to this stream is kept directly. The subscription
 * is kept.
 */
class EventStreamVerifier<E>(
    eventStream: EventStream<E>,
) {
    private val mutableReceivedEvents = mutableListOf<E>()

    private var subscription: Subscription? = eventStream.listenWeak(
        target = this,
    ) { self, event ->
        self.mutableReceivedEvents.add(event)
    }

    fun removeReceivedEvents(): List<E> {
        val receivedEvents = mutableReceivedEvents.toList()

        mutableReceivedEvents.clear()

        return receivedEvents
    }

    fun cancel() {
        val subscription = this.subscription ?: throw IllegalStateException("The subscription is already cancelled")

        subscription.cancel()
        this.subscription = null
    }
}
