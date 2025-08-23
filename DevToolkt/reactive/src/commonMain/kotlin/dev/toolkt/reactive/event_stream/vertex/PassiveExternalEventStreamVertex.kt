package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream

internal class PassiveExternalEventStreamVertex<EventT>(
    private val subscribe: (EventStream.Controller<EventT>) -> Subscription,
) : PassiveEventStreamVertex<EventT>() {
    override fun observe(): Subscription = subscribe(controller)
}
