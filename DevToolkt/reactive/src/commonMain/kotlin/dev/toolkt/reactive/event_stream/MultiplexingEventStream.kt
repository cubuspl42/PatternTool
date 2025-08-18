package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.managed_io.Transaction

// Thought: Merge with DivertEventStream?!
abstract class MultiplexingEventStream<N, E> : DependentEventStream<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedObject.newValues.listen(
            listener = object : UnconditionalListener<N>() {
                override val dependentId = id

                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: N,
                ) {
                    val newInnerObject = event

                    processNewInnerObject(
                        newInnerObject = newInnerObject,
                    )

                    resubscribeToInner(
                        newInnerStream = extractInnerStream(newInnerObject),
                    )
                }
            },
        )

        private var innerSubscription: Subscription = subscribeToInner(
            innerStream = extractInnerStream(nestedObject.currentValueUnmanaged),
        )

        private fun subscribeToInner(
            innerStream: EventStream<E>,
        ): Subscription = innerStream.listen(
            listener = object : UnconditionalListener<E>() {
                override val dependentId = id

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

    protected abstract val nestedObject: Cell<N>

    open fun processNewInnerObject(
        newInnerObject: N,
    ) {
    }

    protected abstract fun extractInnerStream(
        innerObject: N,
    ): EventStream<E>
}
