package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.managed_io.Moments

fun <EventT> EventStream<EventT>.nextExternally(): Future<EventT> = Moments.external {
    next()
}
