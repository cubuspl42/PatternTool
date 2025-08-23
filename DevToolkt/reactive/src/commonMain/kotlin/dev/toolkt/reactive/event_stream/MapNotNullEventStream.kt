package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.vertex.MapNotNullEventStreamVertex

internal class MapNotNullEventStream<EventT, TransformedEventT : Any>(
    source: EventStream<EventT>,
    transform: context(MomentContext) (EventT) -> TransformedEventT?,
) : VertexEventStream<TransformedEventT>() {
    override val vertex = MapNotNullEventStreamVertex(
        source = source,
        transform = transform,
    )
}
