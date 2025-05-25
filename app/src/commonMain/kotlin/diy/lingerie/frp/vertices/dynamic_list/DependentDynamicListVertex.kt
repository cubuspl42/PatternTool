package diy.lingerie.frp.vertices.dynamic_list

import diy.lingerie.frp.vertices.dynamic_list.DynamicListVertex
import diy.lingerie.frp.Subscription

abstract class DependentDynamicListVertex<E>(
    initialElements: List<E>,
) : DynamicListVertex<E>(
    initialElements = initialElements,
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
