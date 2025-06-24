package dev.toolkt.reactive.reactive_list

interface ReactiveListView<out E> {
    val currentElements: List<E>
}
