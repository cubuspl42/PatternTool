package diy.lingerie.reactive.reactive_list

import diy.lingerie.reactive.vertices.dynamic_list.MutableDynamicListVertex

class MutableReactiveList<E>(
    initialElements: List<E>,
) : ActiveReactiveList<E>() {
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
