package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream

internal class PassiveExternalEventStreamVertex<EventT>(
    private val subscribe: (EventStream.Controller<EventT>) -> EventStream.ExternalSubscription,
) : PassiveEventStreamVertex<EventT>() {
    override fun observe(): Subscription {
        val externalSubscription = subscribe(controller)

        externalSubscription.register()

        return object : Subscription {
            override fun cancel() {
                externalSubscription.unregister()
            }
        }
    }
}
