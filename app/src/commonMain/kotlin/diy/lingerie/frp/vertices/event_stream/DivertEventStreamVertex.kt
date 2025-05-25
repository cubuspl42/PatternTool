package diy.lingerie.frp.vertices.event_stream

import diy.lingerie.frp.event_stream.ActiveEventStream
import diy.lingerie.frp.cell.Cell
import diy.lingerie.frp.event_stream.EventStream
import diy.lingerie.frp.Listener
import diy.lingerie.frp.event_stream.NeverEventStream
import diy.lingerie.frp.Subscription
import diy.lingerie.frp.vertices.cell.CellVertex

internal class DivertEventStreamVertex<E>(
    private val nestedEventStream: CellVertex<EventStream<E>>,
) : EventStreamVertex<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedEventStream.subscribeStrong(
            listener = object : Listener<Cell.Change<EventStream<E>>> {
                override fun handle(change: Cell.Change<EventStream<E>>) {
                    val newInnerStream = change.newValue

                    resubscribeToInner(newInnerStream = newInnerStream)
                }
            },
        )

        private var innerSubscription: Subscription = subscribeToInner(
            nestedEventStream.currentValue,
        )

        private fun subscribeToInner(
            innerStream: EventStream<E>,
        ): Subscription = when (innerStream) {
            is ActiveEventStream<E> -> innerStream.vertex.subscribeStrong(
                listener = object : Listener<E> {
                    override fun handle(event: E) {
                        notify(event)
                    }
                },
            )

            NeverEventStream -> Subscription.Noop
        }

        private fun resubscribeToInner(
            newInnerStream: EventStream<E>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerStream = newInnerStream)
        }

        override fun cancel() {
            outerSubscription.cancel()
            innerSubscription.cancel()
        }

    }
}
