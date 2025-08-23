package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

internal class PassiveExternalEventStream<EventT> private constructor(
    private val subscribe: (Controller<EventT>) -> Subscription,
) : PassiveEventStream<EventT>() {
    override fun observe(): Subscription {
        return subscribe(controller)
    }

    companion object {
        fun <EventT> construct(
            subscribe: (Controller<EventT>) -> Subscription,
        ): PassiveExternalEventStream<EventT> = PassiveExternalEventStream(
            subscribe = subscribe,
        )
    }
}
