package diy.lingerie.frp

import diy.lingerie.utils.single

class MutableDynamicListVertex<E>(
    initialElements: List<E>,
) : DynamicListVertex<E>(
    initialElements = initialElements,
) {
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
