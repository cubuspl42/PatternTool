package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream

internal class DivertEventStreamVertex<E>(
    private val source: Cell<EventStream<E>>,
) : PassiveEventStreamVertex<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = source.newValues.listen(
            listener = object : UnconditionalListener<EventStream<E>>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: EventStream<E>,
                ) {
                    resubscribeToInner(
                        newInnerStream = event,
                    )
                }
            },
        )

        private var innerSubscription: Subscription = subscribeToInner(
            innerStream = source.currentValueUnmanaged,
        )

        private fun subscribeToInner(
            innerStream: EventStream<E>,
        ): Subscription = innerStream.listen(
            listener = object : UnconditionalListener<E>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: E,
                ) {
                    notify(
                        transaction = transaction,
                        event = event,
                    )
                }
            },
        )

        private fun resubscribeToInner(
            newInnerStream: EventStream<E>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerStream = newInnerStream)
        }

        override fun cancel() {
            innerSubscription.cancel()
            outerSubscription.cancel()
        }
    }
}
