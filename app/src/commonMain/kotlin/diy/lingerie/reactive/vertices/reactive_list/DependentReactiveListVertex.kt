package diy.lingerie.reactive.vertices.reactive_list

import diy.lingerie.reactive.HybridSubscription
import diy.lingerie.reactive.strengthen
import diy.lingerie.reactive.weaken

/**
 * A vertex of a dynamic list that depends on another vertices (dynamic lists,
 * cells and/or streams), installing a hybrid (weak/strong) subscription to
 * the dependencies. Analogical to [diy.lingerie.reactive.vertices.cell.DependentCellVertex].
 */
abstract class DependentReactiveListVertex<E>(
    initialElements: List<E>,
) : ReactiveListVertex<E>(
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
