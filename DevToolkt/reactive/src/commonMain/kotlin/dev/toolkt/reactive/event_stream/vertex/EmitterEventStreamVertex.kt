package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.effect.Transaction

internal class EmitterEventStreamVertex<EventT>() : EventStreamVertex<EventT>() {
    fun emit(
        transaction: Transaction,
        event: EventT,
    ) {
        notify(
            transaction = transaction,
            event = event,
        )
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
