package diy.lingerie.frp

abstract class DependentCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue = initialValue,
) {
    lateinit var subscription: Subscription

    override fun onResumed() {
        subscription.change(
            strength = Vertex.ListenerStrength.Strong,
        )
    }

    override fun onPaused() {
        subscription.change(
            strength = Vertex.ListenerStrength.Weak,
        )
    }

    protected fun init() {
        subscription = buildInitialSubscription()
    }

    protected abstract fun buildInitialSubscription(): Subscription
}
