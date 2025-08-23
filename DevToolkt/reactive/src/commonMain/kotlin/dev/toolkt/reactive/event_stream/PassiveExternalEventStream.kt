package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertex.PassiveExternalEventStreamVertex

internal class PassiveExternalEventStream<EventT>(
    subscribe: (EventStream.Controller<EventT>) -> EventStream.ExternalSubscription,
) : VertexEventStream<EventT>() {
    override val vertex = PassiveExternalEventStreamVertex(
        subscribe = subscribe,
    )
}
