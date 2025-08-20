package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.effect.MomentContext

internal class SparkEventStream<EventT> private constructor() : ManagedEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> construct(
            event: EventT,
        ): SparkEventStream<EventT> = SparkEventStream<EventT>().apply {
            momentContext.transaction.enqueueFollowup {
                notify(
                    transaction = it,
                    event = event,
                )
            }
        }
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
