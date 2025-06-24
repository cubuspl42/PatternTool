package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.EventStream

abstract class ReactiveListOperator<E> {
    /**
     * A builder for changes that can be used to create a change event
     */
    interface ChangeBuilder<E> {
        companion object {
            fun <E> pure(
                change: ReactiveList.Change<E>,
            ) = object : ChangeBuilder<E> {
                override fun buildChange(
                    currentElements: List<E>,
                ): ReactiveList.Change<E> = change
            }
        }

        fun buildChange(
            /**
             * The current elements of the list (depending on them means that
             * the operator is impure)
             */
            currentElements: List<E>,
        ): ReactiveList.Change<E>?
    }

    abstract fun buildChanges(): EventSource<ChangeBuilder<E>>

    abstract fun getInitialContent(): List<E>
}

abstract class ReactiveListPureOperator<E> : ReactiveListOperator<E>() {
    final override fun buildChanges(): EventSource<ChangeBuilder<E>> = getChanges().map { change ->
        ChangeBuilder.pure(change = change)
    }

    final override fun getInitialContent(): List<E> = getCurrentContent()

    abstract fun getChanges(): EventStream<ReactiveList.Change<E>>

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
