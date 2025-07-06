package dev.toolkt.reactive.reactive_list

abstract class ActiveReactiveList<E>() : ReactiveList<E>() {
    // TODO: Extract a separate class?
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
