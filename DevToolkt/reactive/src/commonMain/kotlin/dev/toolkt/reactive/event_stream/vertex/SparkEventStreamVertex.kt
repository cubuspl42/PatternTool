package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.effect.Transaction

internal class SparkEventStreamVertex<EventT> private constructor() : ActiveEventStreamVertex<EventT>() {
    companion object {
        fun <EventT> construct(
            transaction: Transaction,
            event: EventT,
        ): SparkEventStreamVertex<EventT> = SparkEventStreamVertex<EventT>().apply {
            transaction.enqueueFollowup { followupTransaction ->
                notify(
                    transaction = followupTransaction,
                    event = event,
                )
            }
        }
    }
}
