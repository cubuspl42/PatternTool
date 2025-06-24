package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.EventStream

abstract class ReactiveListOperator<E> {
    abstract fun buildChanges(
        reactiveListView: ReactiveListView<E>,
    ): EventSource<ReactiveList.Change<E>>

    abstract fun getInitialContent(): List<E>
}

abstract class ReactiveListPureOperator<E> : ReactiveListOperator<E>() {
    final override fun buildChanges(
        reactiveListView: ReactiveListView<E>,
    ): EventStream<ReactiveList.Change<E>> = buildChanges()

    final override fun getInitialContent(): List<E> = getCurrentContent()

    abstract fun buildChanges(): EventStream<ReactiveList.Change<E>>

    abstract fun getCurrentContent(): List<E>
}

fun <E> ReactiveListOperator<E>.instantiateCaching(): CachingReactiveList<E> = CachingReactiveList(
    operator = this,
)

fun <E> ReactiveListPureOperator<E>.instantiate(
    behavior: ReactiveList.Behavior,
): ActiveReactiveList<E> = when (behavior) {
    ReactiveList.Behavior.Forward -> ForwardingReactiveList(
        operator = this,
    )

    ReactiveList.Behavior.Cache -> CachingReactiveList(
        operator = this,
    )
}
