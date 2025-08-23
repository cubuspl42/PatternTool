package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.subscribeNow

/**
 * TODO: Remove duplication with [BasicEventStream]
 */
internal abstract class VertexEventStream<EventT> : ProperEventStream<EventT>() {
    final override fun listen(
        listener: Listener<EventT>,
    ): Subscription = vertex.subscribeNow(
        listener = listener,
    )

    abstract val vertex: EventStreamVertex<EventT>
}
