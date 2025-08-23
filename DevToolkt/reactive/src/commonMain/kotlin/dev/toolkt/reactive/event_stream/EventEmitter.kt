package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.vertex.EmitterEventStreamVertex

class EventEmitter<EventT> private constructor() : VertexEventStream<EventT>() {
    companion object {
        /**
         * Creates a new [EventEmitter].
         *
         * [MomentContext] is needed only to give the emitter its identity.
         */
        context(momentContext: MomentContext) fun <EventT> create(): EventEmitter<EventT> = EventEmitter()
    }

    context(actionContext: ActionContext) fun emit(
        event: EventT,
    ) {
        actionContext.transaction.enqueueFollowup { followupTransaction ->
            vertex.emit(
                transaction = followupTransaction,
                event = event,
            )
        }
    }

    val hasListeners: Boolean
        get() = vertex.listenerCount > 0

    override val vertex = EmitterEventStreamVertex<EventT>()
}
