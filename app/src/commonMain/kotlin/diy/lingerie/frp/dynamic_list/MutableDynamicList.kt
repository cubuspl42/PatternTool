package diy.lingerie.frp.dynamic_list

import diy.lingerie.frp.vertices.dynamic_list.MutableDynamicListVertex

class MutableDynamicList<E>(
    initialElements: List<E>,
) : ActiveDynamicList<E>() {
    override val vertex = MutableDynamicListVertex(
        initialElements = initialElements,
    )

    fun set(
        index: Int,
        element: E,
    ) {
        vertex.set(
            index = index,
            element = element,
        )
    }
}
