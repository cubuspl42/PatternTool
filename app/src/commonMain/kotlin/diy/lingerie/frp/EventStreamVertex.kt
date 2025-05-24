package diy.lingerie.frp

abstract class EventStreamVertex<E> : Vertex<E>() {
    private var subscription: Subscription? = null

    final override fun onResumed() {
        if (subscription != null) {
            throw AssertionError("The stream is already resumed (???)")
        }

        subscription = observe()
    }

    final override fun onPaused() {
        val sourceSubscription =
            this.subscription ?: throw AssertionError("The stream is already paused (???)")

        sourceSubscription.cancel()
    }

    protected abstract fun observe(): Subscription
}
