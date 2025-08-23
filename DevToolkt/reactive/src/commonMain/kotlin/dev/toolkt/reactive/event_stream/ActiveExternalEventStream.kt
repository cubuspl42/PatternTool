package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.Effective

internal class ActiveExternalEventStream<EventT> private constructor() : BasicEventStream<EventT>() {
    companion object {
        context(actionContext: ActionContext) fun <EventT> construct(
            activate: (Controller<EventT>) -> Subscription,
        ): Effective<ActiveExternalEventStream<EventT>> = ActiveExternalEventStream<EventT>().let { self ->
            Effective(
                result = self,
                handle = Effect.subscribeExternal {
                    activate(self.controller)
                }
            )
        }
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
