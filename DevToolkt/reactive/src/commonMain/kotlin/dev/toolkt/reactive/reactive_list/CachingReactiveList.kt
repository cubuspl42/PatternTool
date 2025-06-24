package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

class CachingReactiveList<E>(
    operator: ReactiveListOperator<E>,
) : ActiveReactiveList<E>() {
    private val cachedElements = operator.getInitialContent().toMutableList()

    private val changeEmitter = EventEmitter<Change<E>>()

    override val currentElements: List<E>
        get() = cachedElements

    override val changes: EventStream<Change<E>> = changeEmitter

    init {
        operator.buildChanges(
            reactiveListView = this,
        ).listenWeak(
            target = this,
        ) { self, change ->
            changeEmitter.emit(change)

            change.applyTo(
                mutableList = cachedElements,
            )
        }
    }
}
