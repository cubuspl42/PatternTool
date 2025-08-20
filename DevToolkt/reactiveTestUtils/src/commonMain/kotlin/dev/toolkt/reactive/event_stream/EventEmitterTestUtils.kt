package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.Moments
import dev.toolkt.reactive.effect.Actions

fun <EventT> EventEmitter.Companion.createExternally(): EventEmitter<EventT> = Moments.external {
    EventEmitter.create()
}

fun <EventT> EventEmitter<EventT>.emitExternally(
    event: EventT,
) = Actions.external {
    emit(event = event)
}
