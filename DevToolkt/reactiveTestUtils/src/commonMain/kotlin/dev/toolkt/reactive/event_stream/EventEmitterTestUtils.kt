package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Moments
import dev.toolkt.reactive.managed_io.Actions

fun <EventT> EventEmitter.Companion.createExternally(): EventEmitter<EventT> = Moments.external {
    EventEmitter.create()
}

fun <EventT> EventEmitter<EventT>.emitExternally(
    event: EventT,
) = Actions.external {
    emit(event = event)
}
