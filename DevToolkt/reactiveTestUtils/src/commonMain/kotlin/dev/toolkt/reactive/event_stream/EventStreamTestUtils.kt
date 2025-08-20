package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.effect.Moments

fun <EventT> EventStream<EventT>.nextExternally(): Future<EventT> = Moments.external {
    next()
}
