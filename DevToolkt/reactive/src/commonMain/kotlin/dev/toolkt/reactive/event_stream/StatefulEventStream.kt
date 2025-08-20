package dev.toolkt.reactive.event_stream

internal abstract class StatefulEventStream<EventT>() : ManagedEventStream<EventT>() {
    final override fun onResumed() {
    }

    final override fun onPaused() {
    }

    final override fun onAborted() {
    }
}
