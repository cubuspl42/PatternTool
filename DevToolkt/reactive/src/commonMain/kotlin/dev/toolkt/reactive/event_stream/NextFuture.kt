package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.future.ProperFuture

class NextFuture<E>(
    source: EventStream<E>,
) : ProperFuture<E>() {
    override val onResult: EventStream<E> = source.single()

    override val state = onFulfilled.hold(Pending)
}
