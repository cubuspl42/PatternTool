package diy.lingerie.reactive.reactive_list

import diy.lingerie.reactive.vertices.reactive_list.MutableReactiveListVertex

class MutableReactiveList<E>(
    initialElements: List<E>,
) : ActiveReactiveList<E>() {
    override val vertex = MutableReactiveListVertex(
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
