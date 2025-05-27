package diy.lingerie.reactive.vertices.reactive_list

import diy.lingerie.reactive.reactive_list.ReactiveList
import diy.lingerie.reactive.reactive_list.applyTo
import diy.lingerie.reactive.vertices.Vertex

abstract class ReactiveListVertex<E>(
    initialElements: List<E>,
) : Vertex<ReactiveList.Change<E>>() {
    private val mutableElements = initialElements.toMutableList()

    val currentElements: List<E>
        get() = mutableElements.toList()

    protected fun update(
        change: ReactiveList.Change<E>,
    ) {
        change.applyTo(
            mutableList = mutableElements,
        )

        notify(change)
    }
}
