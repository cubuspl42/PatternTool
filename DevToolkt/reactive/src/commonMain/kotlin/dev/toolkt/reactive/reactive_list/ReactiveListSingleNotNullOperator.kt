package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class ReactiveListSingleNotNullOperator<E : Any>(
    private val elementCell: Cell<E?>,
) : ReactiveListOperator<E>() {
    override fun buildChanges(): EventStream<ChangeBuilder<E>> = elementCell.newValues.mapNotNull { newValue ->
        object : ChangeBuilder<E> {
            override fun buildChange(
                currentElements: List<E>,
            ): ReactiveList.Change<E>? {
                val isEmpty = currentElements.isEmpty()

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
                } ?: return null

                return ReactiveList.Change.single(update = update)
            }
        }
    }

    override fun getInitialContent(): List<E> = listOfNotNull(elementCell.currentValue)
}
