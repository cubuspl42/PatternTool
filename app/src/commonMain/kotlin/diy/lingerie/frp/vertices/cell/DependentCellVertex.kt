package diy.lingerie.frp.vertices.cell

import diy.lingerie.frp.vertices.cell.CellVertex
import diy.lingerie.frp.Subscription

abstract class DependentCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue = initialValue,
) {
    lateinit var subscription: Subscription

    override fun onResumed() {
        subscription.change(
            strength = ListenerStrength.Strong,
        )
    }

    override fun onPaused() {
        subscription.change(
            strength = ListenerStrength.Weak,
        )
    }

    protected fun init() {
        subscription = buildInitialSubscription()
    }

    protected abstract fun buildInitialSubscription(): Subscription
}
