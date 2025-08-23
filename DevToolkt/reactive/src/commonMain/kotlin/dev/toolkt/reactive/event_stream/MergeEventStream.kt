package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.MergeEventStreamVertex

internal class MergeEventStream<E>(
    source1: EventStream<E>,
    source2: EventStream<E>,
) : VertexEventStream<E>() {
    override val vertex: EventStreamVertex<E> = MergeEventStreamVertex(
        source1 = source1,
        source2 = source2,
    )
}
