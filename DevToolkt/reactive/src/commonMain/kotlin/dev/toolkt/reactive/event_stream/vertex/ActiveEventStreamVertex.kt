package dev.toolkt.reactive.event_stream.vertex

internal abstract class ActiveEventStreamVertex<EventT> : EventStreamVertex<EventT>() {
    final override fun onResumed() {
    }

    final override fun onPaused() {
    }
}
