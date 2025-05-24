package diy.lingerie.frp

abstract class DependentDynamicListVertex<E>(
    initialElements: List<E>,
) : DynamicListVertex<E>(
    initialElements = initialElements,
) {
    lateinit var subscription: Subscription

    override fun onResumed() {
        subscription.change(
            strength = Notifier.ListenerStrength.Strong,
        )
    }

    override fun onPaused() {
        subscription.change(
            strength = Notifier.ListenerStrength.Weak,
        )
    }

    protected fun init() {
        subscription = buildInitialSubscription()
    }

    protected abstract fun buildInitialSubscription(): Subscription
}
