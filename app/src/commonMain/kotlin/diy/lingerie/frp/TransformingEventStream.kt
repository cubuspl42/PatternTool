package diy.lingerie.frp

abstract class TransformingEventStream<E, Er>(
    private val source: EventStream<E>,
) : NotifyingStream<Er>() {
    private var sourceSubscription: Subscription? = null

    final override fun onResumed() {
        if (sourceSubscription != null) {
            throw AssertionError("The stream is already resumed (???)")
        }

        sourceSubscription = source.subscribe(
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    transformEvent(event)
                }
            },
        )
    }

    final override fun onPaused() {
        val sourceSubscription = this.sourceSubscription ?: throw AssertionError("The stream is already paused (???)")

        sourceSubscription.cancel()
    }

    protected abstract fun transformEvent(event: E)
}
