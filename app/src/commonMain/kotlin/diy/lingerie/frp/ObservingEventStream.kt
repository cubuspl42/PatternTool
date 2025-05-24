package diy.lingerie.frp

abstract class ObservingEventStream<E> : NotifyingStream<E>() {
    private var observationSubscription: Subscription? = null

    final override fun onResumed() {
        if (observationSubscription != null) {
            throw AssertionError("The stream is already resumed (???)")
        }

        observationSubscription = observe()
    }

    final override fun onPaused() {
        val sourceSubscription =
            this.observationSubscription ?: throw AssertionError("The stream is already paused (???)")

        sourceSubscription.cancel()
    }

    protected abstract fun observe(): Subscription
}
