package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.EventStream

class EventStreamVerifier<E>(
    eventStream: EventStream<E>,
) {
    private val mutableReceivedEvents = mutableListOf<E>()

    private val subscription = eventStream.listenWeak(
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
        subscription.cancel()
    }
}
