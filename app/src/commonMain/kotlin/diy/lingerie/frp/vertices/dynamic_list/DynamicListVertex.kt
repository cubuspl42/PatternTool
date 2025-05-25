package diy.lingerie.frp.vertices.dynamic_list

import diy.lingerie.frp.dynamic_list.DynamicList
import diy.lingerie.frp.dynamic_list.applyTo
import diy.lingerie.frp.vertices.Vertex

abstract class DynamicListVertex<E>(
    initialElements: List<E>,
) : Vertex<DynamicList.Change<E>>() {
    private val mutableElements = initialElements.toMutableList()

    val currentElements: List<E>
        get() = mutableElements.toList()

    protected fun update(
        change: DynamicList.Change<E>,
    ) {
        change.applyTo(
            mutableList = mutableElements,
        )

        notify(change)
    }
}
