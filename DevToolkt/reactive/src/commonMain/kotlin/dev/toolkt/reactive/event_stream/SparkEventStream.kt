package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.SparkEventStreamVertex

internal class SparkEventStream<EventT> private constructor(
    override val vertex: EventStreamVertex<EventT>,
) : VertexEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> cause(
            event: EventT,
        ): SparkEventStream<EventT> = SparkEventStream(
            vertex = SparkEventStreamVertex.cause(
                transaction = momentContext.transaction,
                event = event,
            ),
        )
    }
}
