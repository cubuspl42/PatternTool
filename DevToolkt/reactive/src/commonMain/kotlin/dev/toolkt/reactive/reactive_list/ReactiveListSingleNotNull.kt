package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell

class ReactiveListSingleNotNull<ElementT : Any>(
    elementCell: Cell<ElementT?>,
) : AccumulatingReactiveList<ElementT?, ElementT>(
    source = elementCell.newValues,
    initialContent = listOfNotNull(elementCell.currentValue),
) {
    override fun accumulate(
        sourceEvent: ElementT?,
        currentElements: List<ElementT>,
    ): Change<ElementT>? {
        val newValue = sourceEvent

        val isEmpty = currentElements.isEmpty()

        val update = when (newValue) {
            null -> when {
                isEmpty -> null

                else -> Change.Update.remove(
                    index = 0,
                )
            }

            else -> when {
                isEmpty -> Change.Update.insert(
                    index = 0,
                    newElement = newValue,
                )

                else -> Change.Update.set(
                    index = 0,
                    newValue = newValue,
                )
            }
        } ?: return null

        return Change.single(update = update)
    }
}
