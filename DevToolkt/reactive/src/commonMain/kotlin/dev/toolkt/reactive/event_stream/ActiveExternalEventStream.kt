package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.Effective

internal class ActiveExternalEventStream<EventT> private constructor() : BasicEventStream<EventT>() {
    companion object {
        // FIXME: The external stream should be started in the mutation phase, but the subscription should (preferably)
        //  be built up-front (in the propagation phase)
        context(actionContext: ActionContext) fun <EventT> start(
            start: (EventStream.Controller<EventT>) -> Subscription,
        ): Effective<ActiveExternalEventStream<EventT>> = ActiveExternalEventStream<EventT>().let { self ->

            Effective(
                result = self,
                handle = Effect.subscribeExternal {
                    start(self.controller)
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
