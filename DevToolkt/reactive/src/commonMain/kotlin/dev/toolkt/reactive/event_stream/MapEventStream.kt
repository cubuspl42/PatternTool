package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.vertex.MapEventStreamVertex

internal class MapEventStream<EventT, TransformedEventT>(
    source: EventStream<EventT>,
    transform: context(MomentContext) (EventT) -> TransformedEventT,
) : VertexEventStream<TransformedEventT>() {
    override val vertex by lazy {
        MapEventStreamVertex(
            source = source,
            transform = transform,
        )
    }
}
