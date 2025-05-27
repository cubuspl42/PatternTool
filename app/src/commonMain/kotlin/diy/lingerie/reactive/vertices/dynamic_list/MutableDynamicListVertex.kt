package diy.lingerie.reactive.vertices.dynamic_list

import diy.lingerie.reactive.dynamic_list.DynamicList
import dev.toolkt.core.range.single

class MutableDynamicListVertex<E>(
    initialElements: List<E>,
) : DynamicListVertex<E>(
    initialElements = initialElements,
) {
    override val kind: String = "MutableL"

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    fun set(
        index: Int,
        element: E,
    ) {
        this.update(
            change = DynamicList.Change.Update(
                indexRange = IntRange.single(index),
                updatedElements = listOf(element),
            ).toChange(),
        )
    }
}
