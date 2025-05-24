package diy.lingerie.frp

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
