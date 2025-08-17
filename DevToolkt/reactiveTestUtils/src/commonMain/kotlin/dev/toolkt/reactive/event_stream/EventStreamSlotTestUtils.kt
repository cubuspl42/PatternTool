package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Actions

fun <EventT> EventStreamSlot.Companion.createExternally(): EventStreamSlot<EventT> = Actions.external {
    EventStreamSlot.create()
}

fun <EventT> EventStreamSlot<EventT>.bindExternally(
    eventStream: EventStream<EventT>,
) = Actions.external {
    bind(eventStream)
}
