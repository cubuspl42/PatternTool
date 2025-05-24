package diy.lingerie.frp

internal class ProxyEventStream<E>(
    private val source: Notifier<E>,
) : ObservingEventStream<E>() {
    override fun observe(): Subscription = source.subscribe(
        listener = object : Listener<E> {
            override fun handle(event: E) {
                send(event)
            }
        },
    )
}
