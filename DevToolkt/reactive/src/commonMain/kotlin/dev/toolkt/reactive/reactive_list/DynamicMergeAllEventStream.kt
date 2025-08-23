package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.VertexEventStream

internal class DynamicMergeAllEventStream<EventT>(
    eventStreams: ReactiveList<EventStream<EventT>>,
) : VertexEventStream<EventT>() {
    override val vertex = DynamicMergeAllEventStreamVertex(
        eventStreams = eventStreams,
    )
}
