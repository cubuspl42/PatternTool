package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class ReactiveListSingleNotNullOperator<E : Any>(
    private val elementCell: Cell<E?>,
) : ReactiveListOperator<E>() {
    override fun buildChanges(
        reactiveListView: ReactiveListView<E>,
    ): EventStream<ReactiveList.Change<E>> = elementCell.newValues.mapNotNull { newValue ->
        val isEmpty = reactiveListView.currentElements.isEmpty()

        val update = when (newValue) {
            null -> when {
                isEmpty -> null

                else -> ReactiveList.Change.Update.remove(
                    index = 0,
                )
            }

            else -> when {
                isEmpty -> ReactiveList.Change.Update.insert(
                    index = 0,
                    newElement = newValue,
                )

                else -> ReactiveList.Change.Update.set(
                    index = 0,
                    newValue = newValue,
                )
            }
        } ?: return@mapNotNull null

        ReactiveList.Change.Companion.single(update = update)
    }

    override fun getInitialContent(): List<E> = listOfNotNull(elementCell.currentValue)
}
