package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
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

    this@getEventStream.addEventListener(
        type = type,
        callback = ::callback,
        options = AddEventListenerOptions(
            passive = true,
        ),
    )

    object : Subscription {
        override fun cancel() {
            this@getEventStream.removeEventListener(
                type = type,
                callback = ::callback,
            )
        }
    }
}
