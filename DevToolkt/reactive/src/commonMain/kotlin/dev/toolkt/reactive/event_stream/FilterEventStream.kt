package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.vertex.FilterEventStreamVertex

internal class FilterEventStream<EventT>(
    source: EventStream<EventT>,
    predicate: context(MomentContext) (EventT) -> Boolean,
) : VertexEventStream<EventT>() {
    override val vertex by lazy {
        FilterEventStreamVertex(
            source = source,
            predicate = predicate,
        )
    }
}
