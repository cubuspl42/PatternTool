package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.managed_io.Proactions
import dev.toolkt.reactive.managed_io.Reactions

fun <EventT> EventEmitter.Companion.createExternally(): EventEmitter<EventT> = Reactions.external {
    EventEmitter.create()
}

fun <EventT> EventEmitter<EventT>.emitExternally(
    event: EventT,
) = Proactions.external {
    emitUnmanaged(event = event)
}
