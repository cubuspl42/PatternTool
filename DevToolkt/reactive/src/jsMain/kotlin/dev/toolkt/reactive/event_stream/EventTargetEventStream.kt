package dev.toolkt.reactive.event_stream

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

class EventTargetEventStream(
    private val eventTarget: EventTarget,
    private val type: String,
) : ForeignEventStream<Event>() {
    override fun onResumed() {
        TODO("Not yet implemented")
    }

    override fun onPaused() {
        TODO("Not yet implemented")
    }

    override val successorEventStream: EventStream<Event>?
        get() = TODO("Not yet implemented")
}

fun EventTarget.getEventStream(
    type: String,
): EventStream<Event> = EventTargetEventStream(
    eventTarget = this,
    type = type,
)
