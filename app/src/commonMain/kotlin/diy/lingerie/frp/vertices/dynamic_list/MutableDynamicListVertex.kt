package diy.lingerie.frp.vertices.dynamic_list

import diy.lingerie.frp.dynamic_list.DynamicList
import diy.lingerie.utils.single

class MutableDynamicListVertex<E>(
    initialElements: List<E>,
) : DynamicListVertex<E>(
    initialElements = initialElements,
) {
    override val kind: String = "MutableL"

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
