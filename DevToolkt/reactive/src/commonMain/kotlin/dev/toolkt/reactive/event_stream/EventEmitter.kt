package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext

class EventEmitter<EventT> private constructor() : BasicEventStream<EventT>() {
    companion object {
        /**
         * Creates a new [EventEmitter].
         *
         * [MomentContext] is needed only to give the emitter its identity.
         */
        context(momentContext: MomentContext) fun <EventT> create(): EventEmitter<EventT> = EventEmitter()
    }

    context(actionContext: ActionContext) fun emit(event: EventT) {
        // FIXME: Enqueue this! Test should catch this
        notify(
            transaction = actionContext.transaction,
            event = event,
        )
    }

    val hasListeners: Boolean
        get() = listenerCount > 0

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
