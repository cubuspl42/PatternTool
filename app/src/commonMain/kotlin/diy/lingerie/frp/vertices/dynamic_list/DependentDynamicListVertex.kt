package diy.lingerie.frp.vertices.dynamic_list

import diy.lingerie.frp.HybridSubscription
import diy.lingerie.frp.strengthen
import diy.lingerie.frp.weaken

/**
 * A vertex of a dynamic list that depends on another vertices (dynamic lists,
 * cells and/or streams), installing a hybrid (weak/strong) subscription to
 * the dependencies. Analogical to [diy.lingerie.frp.vertices.cell.DependentCellVertex].
 */
abstract class DependentDynamicListVertex<E>(
    initialElements: List<E>,
) : DynamicListVertex<E>(
    initialElements = initialElements,
) {
    lateinit var subscription: HybridSubscription

    final override fun onResumed() {
        subscription.strengthen()
    }

    final override fun onPaused() {
        subscription.weaken()
    }

    protected fun init() {
        subscription = buildHybridSubscription()
    }

    protected abstract fun buildHybridSubscription(): HybridSubscription
}
