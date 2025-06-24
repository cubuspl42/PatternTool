package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList.Change

class ReactiveListSingleOperator<E>(
    private val elementCell: Cell<E>,
) : ReactiveListPureOperator<E>() {
    override fun buildChanges(): EventStream<Change<E>> = elementCell.newValues.map { newValue ->
        ReactiveList.Change.Companion.single(
            update = ReactiveList.Change.Update.set(
                index = 0,
                newValue = newValue,
            ),
        )
    }

    override fun getCurrentContent(): List<E> = listOf(elementCell.currentValue)
}
