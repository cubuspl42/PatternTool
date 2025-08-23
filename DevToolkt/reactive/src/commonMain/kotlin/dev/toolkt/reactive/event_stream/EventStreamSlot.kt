package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.vertex.DivertEventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex

class EventStreamSlot<EventT> private constructor(
    private val boundEventStream: MutableCell<EventStream<EventT>>,
) : VertexEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> create(): EventStreamSlot<EventT> = EventStreamSlot(
            boundEventStream = MutableCell.create(
                initialValue = EventStream.Never,
            ),
        )
    }

    override val vertex: EventStreamVertex<EventT> = DivertEventStreamVertex(
        source = boundEventStream,
    )

    context(actionContext: ActionContext) fun bind(
        eventStream: EventStream<EventT>,
    ) {
        boundEventStream.set(eventStream)
    }
}
