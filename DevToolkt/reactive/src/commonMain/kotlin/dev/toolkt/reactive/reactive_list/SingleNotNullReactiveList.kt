package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class SingleNotNullReactiveList<ElementT : Any>(
    private val elementCell: Cell<ElementT?>,
) : ActiveReactiveList<ElementT>() {
    override val changes: EventStream<Change<ElementT>> = elementCell.newValues.mapNotNull { newValue ->
        val oldValue = elementCell.currentValueUnmanaged

        val update = when (newValue) {
            null -> when (oldValue) {
                null -> null

                else -> Change.Update.remove(
                    index = 0,
                )
            }

            else -> when (oldValue) {
                null -> Change.Update.insert(
                    index = 0,
                    newElement = newValue,
                )

                else -> Change.Update.set(
                    index = 0,
                    newValue = newValue,
                )
            }
        } ?: return@mapNotNull null

        Change.single(update = update)
    }

    override val currentElements: List<ElementT>
        get() = listOfNotNull(elementCell.currentValueUnmanaged)
}
