package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.vertex.PassiveExternalEventStreamVertex

internal class PassiveExternalEventStream<EventT>(
    subscribe: (EventStream.Controller<EventT>) -> Subscription,
) : VertexEventStream<EventT>() {
    override val vertex = PassiveExternalEventStreamVertex(
        subscribe = subscribe,
    )
}
