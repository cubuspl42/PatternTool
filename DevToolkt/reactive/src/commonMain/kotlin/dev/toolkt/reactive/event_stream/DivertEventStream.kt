package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell

class DivertEventStream<E>(
    private val nestedEventStream: Cell<EventStream<E>>,
) : DependentEventStream<E>() {
    override fun observe(): Subscription {
        // Initial inner subscription could be null if the current inner stream
        // has ended, but we don't know anything about the inner streams to come
        var innerSubscription = subscribeToInner(
            innerStream = nestedEventStream.currentValue,
        )

        val outerSubscription = nestedEventStream.newValues.subscribeStrong(
            eventHandler = object : EventHandler<EventStream<E>> {
                override fun handleEvent(
                    source: EventSource<EventStream<E>>,
                    event: EventStream<E>,
                ) {
                    innerSubscription?.cancel()

                    val newInnerStream = event
                    innerSubscription = subscribeToInner(innerStream = newInnerStream)
                }

                override fun handleStop(
                    source: EventSource<EventStream<E>>,
                ) {
                    nestedEventStream.currentValue.internalizeIfProper(this@DivertEventStream)
                }
            },
        ) ?: throw AssertionError()

        return object : Subscription {
            override fun cancel() {
                outerSubscription.cancel()
                innerSubscription?.cancel()
            }
        }
    }

    private fun subscribeToInner(
        innerStream: EventStream<E>,
    ): Subscription? = innerStream.subscribeStrong(
        eventHandler = object : EventHandler<E> {
            override fun handleEvent(
                source: EventSource<E>,
                event: E,
            ) {
                notify(event)
            }

            override fun handleStop(source: EventSource<E>) {
            }
        },
    )

    override val successorEventStream: EventStream<E>?
        get() = when {
            nestedEventStream.isFinal -> nestedEventStream.currentValue
            else -> null
        }
}
