package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream

class DynamicMergeAllEventStream<EventT>(
    private val eventStreams: ReactiveList<EventStream<EventT>>,
) : DependentEventStream<EventT>() {
    override fun observe(): Subscription = object : Subscription {
        override fun cancel() {
            TODO("Not yet implemented")
        }
    }
}
