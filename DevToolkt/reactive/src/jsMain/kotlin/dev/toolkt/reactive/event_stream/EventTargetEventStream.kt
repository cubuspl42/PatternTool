package dev.toolkt.reactive.event_stream

import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

fun EventTarget.getEventStream(
    type: String,
): EventStream<Event> = EventStream.subscribeExternal { controller ->
    fun callback(
        event: Event,
    ) {
        controller.accept(
            event = event,
        )
    }

    object : EventStream.ExternalSubscription {
        override fun register() {
            this@getEventStream.addEventListener(
                type = type,
                callback = ::callback,
                options = AddEventListenerOptions(
                    passive = true,
                ),
            )
        }

        override fun unregister() {
            this@getEventStream.removeEventListener(
                type = type,
                callback = ::callback,
            )
        }
    }
}
