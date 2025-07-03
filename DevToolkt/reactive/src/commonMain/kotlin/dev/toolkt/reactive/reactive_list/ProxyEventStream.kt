package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.listen

abstract class ProxyEventStream<EventT>(
    private val source: EventSource<EventT>,
) : DependentEventStream<EventT>() {
    override fun observe(): Subscription = source.listen { event ->
        notify(event = event)

        onNotified(event = event)
    }

    abstract fun onNotified(event: EventT)
}
