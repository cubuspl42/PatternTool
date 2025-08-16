package dev.toolkt.reactive.event_stream

class EventEmitter<E> : ManagedEventStream<E>() {
    fun emit(event: E) {
        notify(event)
    }

    override val hasListeners: Boolean
        get() = listenerCount > 0

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
