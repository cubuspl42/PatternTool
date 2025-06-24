package dev.toolkt.reactive.reactive_list

abstract class ActiveReactiveList<E>() : ReactiveList<E>() {
    final override fun <Er> map(
        behavior: Behavior,
        transform: (E) -> Er,
    ): ReactiveList<Er> = ReactiveListPureMapOperator(
        source = this,
        transform = transform,
    ).instantiate(
        behavior = behavior,
    )
}
