package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

class TakeEventStream<E>(
    private val source: EventStream<E>,
    count: Int,
) : StatefulEventStream<E>() {
    private var remainingCount: Int = count

    override fun observeStateful(): Subscription = source.listen { sourceEvent ->
        val remainingCount = this.remainingCount

        if (remainingCount <= 0) {
            throw IllegalStateException("No more remaining events to take")
        }

        val newRemainingCount = remainingCount - 1
        this.remainingCount = newRemainingCount

        this.notify(event = sourceEvent)

        if (newRemainingCount == 0) {
            abort()
        }
    }

    init {
        require(count > 0)

        init()
    }
}
