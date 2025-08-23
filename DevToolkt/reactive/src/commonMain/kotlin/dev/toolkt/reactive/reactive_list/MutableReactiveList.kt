package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.append
import dev.toolkt.core.iterable.removeRange
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

class MutableReactiveList<ElementT> private constructor(
    private val changeEmitter: EventEmitter<ReactiveList.Change<ElementT>>,
    initialContent: List<ElementT>,
) : ProperReactiveList<ElementT>() {
    companion object {
        context(momentContext: MomentContext) fun <ElementT> create(
            initialContent: List<ElementT>,
        ): MutableReactiveList<ElementT> = MutableReactiveList(
            changeEmitter = EventEmitter.create(),
            initialContent = initialContent,
        )
    }

    private val mutableContent = initialContent.toMutableList()

    override val changes: EventStream<Change<ElementT>>
        get() = changeEmitter

    override val currentElementsUnmanaged: List<ElementT>
        get() = mutableContent.toList()

    context(actionContext: ActionContext) fun set(
        index: Int,
        newValue: ElementT,
    ) {
        if (index !in mutableContent.indices) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.set(
            index = index,
            newValue = newValue,
        )

        changeEmitter.emit(
            Change.single(
                update = update,
            ),
        )

        actionContext.enqueueMutation {
            mutableContent[index] = newValue
        }
    }

    context(actionContext: ActionContext) fun add(
        index: Int,
        element: ElementT,
    ) {
        addAll(
            index = index,
            elements = listOf(element),
        )
    }

    context(actionContext: ActionContext) fun addAll(
        index: Int,
        elements: List<ElementT>,
    ) {
        if (index !in 0..mutableContent.size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.insert(
            index = index,
            newElements = elements,
        )

        val change = Change.single(
            update = update,
        )

        changeEmitter.emit(change)

        actionContext.enqueueMutation {
            mutableContent.addAll(
                index = index,
                elements = elements,
            )
        }
    }

    context(actionContext: ActionContext) fun replaceAll(
        indexRange: IntRange,
        changedElements: List<ElementT>,
    ) {
        if (indexRange.first !in 0..mutableContent.size || indexRange.last !in 0 until mutableContent.size) {
            throw IndexOutOfBoundsException("Index range $indexRange is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.change(
            indexRange = indexRange,
            changedElements = changedElements,
        )

        val change = Change.single(
            update = update,
        )

        changeEmitter.emit(change)

        actionContext.enqueueMutation {
            mutableContent.removeRange(indexRange)

            mutableContent.addAll(
                index = indexRange.first,
                elements = changedElements,
            )
        }
    }

    context(actionContext: ActionContext) fun append(
        element: ElementT,
    ) {
        val update = Change.Update.insert(
            index = currentElementsUnmanaged.size,
            newElements = listOf(element),
        )

        val change = Change.single(
            update = update,
        )

        changeEmitter.emit(change)

        actionContext.enqueueMutation {
            mutableContent.append(element)
        }
    }

    context(actionContext: ActionContext) fun removeRange(indexRange: IntRange) {
        if (indexRange.first !in 0..mutableContent.size || indexRange.last !in 0 until mutableContent.size) {
            throw IndexOutOfBoundsException("Index range $indexRange is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.remove(
            indexRange = indexRange,
        )

        changeEmitter.emit(
            Change.single(
                update = update,
            ),
        )

        actionContext.enqueueMutation {
            mutableContent.removeRange(indexRange)
        }
    }

    context(actionContext: ActionContext) fun removeAt(index: Int) {
        if (index !in mutableContent.indices) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.remove(
            index = index,
        )

        changeEmitter.emit(
            Change.single(
                update = update,
            ),
        )

        actionContext.enqueueMutation {
            mutableContent.removeAt(index = index)
        }
    }
}
