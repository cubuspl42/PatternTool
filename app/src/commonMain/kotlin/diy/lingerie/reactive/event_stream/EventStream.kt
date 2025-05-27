package diy.lingerie.reactive.event_stream

import diy.lingerie.reactive.vertices.cell.HoldCellVertex
import diy.lingerie.reactive.cell.ActiveCell
import diy.lingerie.reactive.cell.Cell
import diy.lingerie.reactive.cell.ConstCell
import diy.lingerie.reactive.cell.DependentCell
import diy.lingerie.reactive.vertices.event_stream.DivertEventStreamVertex

sealed class EventStream<out E> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> = when (nestedEventStream) {
            is ActiveCell<EventStream<V>> -> DependentEventStream(
                vertex = DivertEventStreamVertex(
                    nestedEventStream = nestedEventStream.vertex,
                ),
            )

            is ConstCell<EventStream<V>> -> nestedEventStream.constValue
        }
    }

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er>

    abstract fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E>

    abstract fun <T : Any> pipe(
        target: T,
        consume: (E) -> Unit,
    )
}

fun <E> EventStream<E>.hold(
    initialValue: E,
): Cell<E> = when (this) {
    is ActiveEventStream<E> -> DependentCell(
        HoldCellVertex(
            values = this.vertex,
            initialValue = initialValue,
        )
    )

    NeverEventStream -> ConstCell(
        constValue = initialValue,
    )
}
