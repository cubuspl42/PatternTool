package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.updateRange
import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

abstract class ReactiveList<out E> : ReactiveListView<E> {
    data class Change<out E>(
        /**
         * The update this change consists of. In the future, a change might
         * consider of multiple updates.
         */
        val update: Update<E>,
    ) {
        data class Update<out E>(
            val indexRange: IntRange,
            val updatedElements: List<E>,
        ) {
            companion object {
                fun <E> change(
                    indexRange: IntRange,
                    changedElements: List<E>,
                ): Update<E> = Update(
                    indexRange = indexRange,
                    updatedElements = changedElements,
                )

                fun <E> set(
                    index: Int,
                    newValue: E,
                ): Update<E> = Update(
                    indexRange = IntRange.single(index),
                    updatedElements = listOf(newValue),
                )

                fun remove(
                    index: Int,
                ): Update<Nothing> = remove(
                    indexRange = IntRange.single(index),
                )

                fun remove(
                    indexRange: IntRange,
                ): Update<Nothing> = Update(
                    indexRange = indexRange,
                    updatedElements = emptyList(),
                )

                fun <E> insert(
                    index: Int,
                    newElement: E,
                ): Update<E> = insert(
                    index = index,
                    newElements = listOf(newElement),
                )

                fun <E> insert(
                    index: Int,
                    newElements: List<E>,
                ): Update<E> = Update(
                    indexRange = IntRange.empty(index),
                    updatedElements = newElements,
                )
            }

            fun <Er> map(
                transform: (E) -> Er,
            ): Update<Er> = Update(
                indexRange = indexRange,
                updatedElements = updatedElements.map(transform),
            )

            val isEffective: Boolean
                get() = !indexRange.isEmpty() || updatedElements.isNotEmpty()
        }

        companion object {
            fun <E> single(
                update: Update<E>,
            ): Change<E> = Change(
                update = update,
            )

            fun <E> fill(
                elements: List<E>,
            ): Change<E>? = single(
                update = Update.insert(
                    index = 0,
                    newElements = elements,
                ),
            )
        }

        val updatesInOrder: List<Update<E>>
            get() = listOf(update)

        fun <Er> map(
            transform: (E) -> Er,
        ): Change<Er> = Change(
            update = update.map(transform),
        )

        init {
            require(update.isEffective)
        }
    }

    enum class Behavior {
        Forward, Cache,
    }

    companion object {
        fun <E> of(
            vararg children: E,
        ): ReactiveList<E> = ConstReactiveList(
            constElements = children.toList(),
        )

        fun <E> single(
            element: Cell<E>,
            behavior: Behavior = Behavior.Forward,
        ): ReactiveList<E> = ReactiveListSingleOperator(
            elementCell = element,
        ).instantiate(
            behavior = behavior,
        )

        fun <E : Any> singleNotNull(
            element: Cell<E?>,
        ): ReactiveList<E> = ReactiveListSingleNotNullOperator(
            elementCell = element,
        ).instantiateCaching()

        fun <E> fuse(
            cells: ReactiveList<Cell<E>>,
            behavior: Behavior = Behavior.Forward,
        ): ReactiveList<E> = FuseReactiveListOperator(
            source = cells,
        ).instantiate(
            behavior = behavior,
        )

        fun <E, R> looped(
            block: (ReactiveList<E>) -> Pair<R, ReactiveList<E>>,
        ): R {
            val loopedReactiveList = LoopedReactiveList<E>()

            val (result, reactiveList) = block(loopedReactiveList)

            loopedReactiveList.loop(reactiveList)

            return result
        }
    }

    abstract val changes: EventStream<Change<E>>

    abstract fun <Er> map(
        behavior: Behavior = Behavior.Forward,
        transform: (E) -> Er,
    ): ReactiveList<Er>
}

internal fun <E> ReactiveList<E>.copyNow(
    mutableList: MutableList<E>,
) {
    mutableList.clear()
    mutableList.addAll(currentElements)
}

fun <E, T : Any> ReactiveList<E>.bind(
    target: T,
    extract: (T) -> MutableList<in E>,
) {
    copyNow(mutableList = extract(target))

    changes.pipe(
        target = target,
    ) { target, change ->
        change.applyTo(mutableList = extract(target))
    }
}

fun <E> ReactiveList.Change.Update<E>.applyTo(
    mutableList: MutableList<E>,
) {
    mutableList.updateRange(
        indexRange = indexRange,
        elements = updatedElements,
    )
}

fun <E> ReactiveList.Change<E>.applyTo(
    mutableList: MutableList<E>,
) {
    updatesInOrder.reversed().forEach { update ->
        update.applyTo(mutableList = mutableList)
    }
}
