package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertex.StaticMergeAllEventStreamVertex

internal class StaticMergeAllEventStream<EventT>(
    private val sources: List<EventStream<EventT>>,
) : VertexEventStream<EventT>() {
    override val vertex by lazy {
        StaticMergeAllEventStreamVertex(sources = sources)
    }
}
