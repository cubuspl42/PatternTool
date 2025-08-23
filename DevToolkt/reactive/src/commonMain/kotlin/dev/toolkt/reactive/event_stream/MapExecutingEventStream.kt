package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effective
import dev.toolkt.reactive.event_stream.vertex.MapExecutingEventStreamVertex
import dev.toolkt.reactive.toEffectHandle

internal class MapExecutingEventStream<TransformedEventT> private constructor(
    override val vertex: MapExecutingEventStreamVertex<TransformedEventT>,
) : VertexEventStream<TransformedEventT>() {
    companion object {
        context(actionContext: ActionContext) fun <EventT, TransformedEventT> start(
            source: EventStream<EventT>,
            transform: context(ActionContext) (EventT) -> TransformedEventT,
        ): Effective<MapExecutingEventStream<TransformedEventT>> {
            val (vertex, subscription) = MapExecutingEventStreamVertex.start(
                transaction = actionContext.transaction,
                source = source,
                transform = transform,
            )

            return Effective(
                result = MapExecutingEventStream(
                    vertex = vertex,
                ),
                handle = subscription.toEffectHandle(),
            )
        }
    }
}
