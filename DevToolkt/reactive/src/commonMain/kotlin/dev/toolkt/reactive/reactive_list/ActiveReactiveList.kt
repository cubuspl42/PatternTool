package dev.toolkt.reactive.reactive_list

abstract class ActiveReactiveList<E>() : ReactiveList<E>() {
    final override fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveList<Er> = MapReactiveList(
        source = this,
        transform = transform,
    )
}
