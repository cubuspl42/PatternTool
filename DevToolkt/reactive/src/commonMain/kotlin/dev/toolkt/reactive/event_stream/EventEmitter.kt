package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.ActionContext

// TODO: Make the constructor private
class EventEmitter<EventT> : ManagedEventStream<EventT>() {
    companion object {
        /**
         * Creates a new [EventEmitter].
         *
         * [MomentContext] is needed only to give the emitter its identity.
         */
        context(momentContext: MomentContext) fun <EventT> create(): EventEmitter<EventT> = EventEmitter()
    }

    fun emitUnmanaged(event: EventT) {
        notify(
            transaction = TODO("Nuke unmanaged code"),
            event,
        )
    }

    context(actionContext: ActionContext) fun emit(event: EventT) {
        // FIXME: This should make the tests fail
        emitUnmanaged(event)
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
