package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.ProactionContext

// TODO: Make the constructor private
class EventEmitter<EventT> : ManagedEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> create(): EventEmitter<EventT> {
            TODO()
        }
    }

    fun emitUnmanaged(event: EventT) {
        notify(event)
    }

    context(proactionContext: ProactionContext) fun emit(event: EventT) {
        TODO()
    }

    val hasListeners: Boolean
        get() = listenerCount > 0

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
