package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream

internal class MergeEventStreamVertex<EventT>(
    private val source1: EventStream<EventT>,
    private val source2: EventStream<EventT>,
) : PassiveEventStreamVertex<EventT>() {
    override fun observe(): Subscription = object : Subscription {
        private val subscription1 = source1.listen(
            listener = object : UnconditionalListener<EventT>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: EventT,
                ) {
                    notify(
                        transaction = transaction,
                        event = event,
                    )
                }
            },
        )

        private val subscription2 = source2.listen(
            listener = object : UnconditionalListener<EventT>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: EventT,
                ) {
                    notify(
                        transaction = transaction,
                        event = event,
                    )
                }
            },
        )

        override fun cancel() {
            subscription1.cancel()
            subscription2.cancel()
        }
    }
}
