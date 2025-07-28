package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.event_stream.EventStreamWeak

/**
 * A verifier for an [EventStream] that collects all received events through a weak listener.
 *
 * @constructor
 * @param eventStream the [EventStream] to verify. No strong reference to this stream is kept directly. The subscription
 * is discarded.
 */
class DetachedEventStreamVerifier<E>(
    eventStream: EventStream<E>,
) {
    private val mutableReceivedEvents = mutableListOf<E>()

    init {
        eventStream.listenWeak(
            target = this,
        ) { self, event ->
            self.mutableReceivedEvents.add(event)
        }
    }

    fun removeReceivedEvents(): List<E> {
        val receivedEvents = mutableReceivedEvents.toList()

        mutableReceivedEvents.clear()

        return receivedEvents
    }
}
