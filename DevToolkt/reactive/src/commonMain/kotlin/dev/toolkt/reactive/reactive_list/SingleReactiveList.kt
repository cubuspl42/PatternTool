package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class SingleReactiveList<ElementT>(
    private val elementCell: Cell<ElementT>,
) : ActiveReactiveList<ElementT>() {
    override val changes: EventStream<Change<ElementT>> = elementCell.newValues.mapNotNull { newValue ->
        val update = Change.Update.set(
            index = 0,
            newValue = newValue,
        )

        Change.single(update = update)
    }

    override val currentElements: List<ElementT>
        get() = listOf(elementCell.currentValue)
}
