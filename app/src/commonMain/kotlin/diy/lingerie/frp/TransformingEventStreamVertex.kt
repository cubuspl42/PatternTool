package diy.lingerie.frp

abstract class TransformingEventStreamVertex<E, Er>(
    private val source: Vertex<E>,
) : EventStreamVertex<Er>() {
    protected abstract fun handleSourceEvent(event: E)

    override fun observe(): Subscription = source.subscribe(
        listener = object : Listener<E> {
            override fun handle(event: E) {
                handleSourceEvent(event)
            }
        },
    )
}
