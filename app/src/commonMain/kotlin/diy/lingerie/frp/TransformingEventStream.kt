package diy.lingerie.frp

abstract class TransformingEventStream<E, Er>(
    private val source: EventStream<E>,
) : ObservingEventStream<Er>() {
    protected abstract fun handleSourceEvent(event: E)

    override fun observe(): Subscription = source.subscribe(
        listener = object : Listener<E> {
            override fun handle(event: E) {
                handleSourceEvent(event)
            }
        },
    )
}
