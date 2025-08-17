package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Reactions

fun <EventT> EventStreamSlot.Companion.createExternally(): EventStreamSlot<EventT> = Reactions.external {
    EventStreamSlot.create()
}

fun <EventT> EventStreamSlot<EventT>.bindExternally(
    eventStream: EventStream<EventT>,
) = Reactions.external {
    bind(eventStream)
}
