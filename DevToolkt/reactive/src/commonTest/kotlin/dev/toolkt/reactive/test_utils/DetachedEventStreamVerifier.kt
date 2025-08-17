package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.listenWeak

/**
 * A verifier for an [EventStream] that collects all received events through a weak listener.
 *
 * TODO: Figure out if this makes sense with the new contracts. Now the subscription is potentially the only object
 *  that has a reference to the stream being listened to (in the weak mode). That subscriptions have to be kept
 *  somewhere, even when it's the captured context of a cleanup lambda in the finalization registry.
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
