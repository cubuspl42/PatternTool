package diy.lingerie.frp

internal class DivertEventStreamVertex<E>(
    private val nestedEventStream: CellVertex<EventStream<E>>,
) : EventStreamVertex<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedEventStream.subscribe(
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
            is ActiveEventStream<E> -> innerStream.vertex.subscribe(
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

        override fun change(strength: Vertex.ListenerStrength) {
            outerSubscription.change(strength = strength)
            innerSubscription.change(strength = strength)
        }
    }
}
