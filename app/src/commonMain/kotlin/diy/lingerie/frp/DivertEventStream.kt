package diy.lingerie.frp

internal class DivertEventStream<E>(
    private val nestedEventStream: Cell<EventStream<E>>,
) : ObservingEventStream<E>() {
    override fun observe(): Subscription = object : Subscription {
        val outerSubscription = nestedEventStream.newValues.subscribe(
            listener = object : Listener<EventStream<E>> {
                override fun handle(newInnerStream: EventStream<E>) {
                    resubscribeToInner(newInnerStream = newInnerStream)
                }
            },
        )

        private var innerSubscription: Subscription = subscribeToInner(
            nestedEventStream.currentValue,
        )

        private fun subscribeToInner(
            innerStream: EventStream<E>,
        ): Subscription = innerStream.subscribe(
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    notify(event)
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
            outerSubscription.cancel()
            innerSubscription.cancel()
        }
    }
}
