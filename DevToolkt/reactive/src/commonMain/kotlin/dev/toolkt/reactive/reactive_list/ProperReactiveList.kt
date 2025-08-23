package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.delegates.weakLazy
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.holdUnmanaged

abstract class ProperReactiveList<E>() : ReactiveList<E>() {
    final override val newElements: EventStream<List<E>> by lazy {
        changes.map { change ->
            val oldElements = currentElementsUnmanaged.toMutableList()

            change.applyTo(oldElements)

            oldElements.toList()
        }
    }

    final override val elements: Cell<List<E>> by weakLazy {
        newElements.holdUnmanaged(currentElementsUnmanaged)
    }

    final override fun <Er> map(
        behavior: Behavior,
        transform: (E) -> Er,
    ): ReactiveList<Er> = MapReactiveList(
        source = this,
        transform = transform,
    )
}
