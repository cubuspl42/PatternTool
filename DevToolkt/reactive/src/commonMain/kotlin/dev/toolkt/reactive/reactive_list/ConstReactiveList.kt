package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

data class ConstReactiveList<out E>(
    private val constElements: List<E>,
) : ReactiveList<E>() {
    override val currentElements: List<E>
        get() = constElements

    override val elements: Cell<List<E>> = Cell.of(constElements)

    override val newElements: EventStream<List<E>> = EventStream.Never

    override val changes: EventStream<Change<E>> = EventStream.Never

    override fun <Er> map(
        behavior: Behavior,
        transform: (E) -> Er,
    ): ReactiveList<Er> = ConstReactiveList(
        constElements = constElements.map(transform),
    )
}
