package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.vertex.DivertEventStreamVertex

internal class DivertEventStream<EventT>(
    source: Cell<EventStream<EventT>>,
) : VertexEventStream<EventT>() {
    override val vertex by lazy {
        DivertEventStreamVertex(
            source = source,
        )
    }
}
