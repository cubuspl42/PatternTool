package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.updateRange
import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.forEach
import dev.toolkt.reactive.event_stream.holdUnmanaged
import dev.toolkt.reactive.managed_io.Actions
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Trigger
import dev.toolkt.reactive.managed_io.map

abstract class ReactiveList<out E> {
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
    }

    enum class Behavior {
        Forward, Cache,
    }

    companion object {
        val Empty = of(
            children = emptyList<Nothing>(),
        )

        fun <E> of(
            children: List<E>,
        ): ReactiveList<E> = ConstReactiveList(
            constElements = children,
        )

        fun <E> of(
            vararg children: E,
        ): ReactiveList<E> = ConstReactiveList(
            constElements = children.toList(),
        )

        fun <E> single(
            element: Cell<E>,
        ): ReactiveList<E> = SingleReactiveList(
            elementCell = element,
        )

        fun <E : Any> singleNotNull(
            element: Cell<E?>,
        ): ReactiveList<E> = SingleNotNullReactiveList(
            elementCell = element,
        )

        fun <E> diff(
            listCell: Cell<List<E>>,
        ): ReactiveList<E> = DiffReactiveList(
            source = listCell,
        )

        fun <ElementT> diffDynamic(
            reactiveListCell: Cell<ReactiveList<ElementT>>,
        ): ReactiveList<ElementT> = DynamicDiffReactiveList(
            source = reactiveListCell,
        )

        fun <E> fuse(
            vararg cells: Cell<E>,
        ): ReactiveList<E> = fuse(
            cells = cells.toList(),
        )

        fun <E> fuse(
            cells: List<Cell<E>>,
        ): ReactiveList<E> = fuse(
            of(*cells.toTypedArray()),
        )

        fun <E> fuse(
            cells: ReactiveList<Cell<E>>,
        ): ReactiveList<E> = FuseReactiveList(
            source = cells,
        )

        fun <ElementT> concatAll(
            vararg lists: ReactiveList<ElementT>,
        ): ReactiveList<ElementT> = concatAll(
            lists = lists.toList(),
        )

        fun <ElementT> concatAll(
            lists: List<ReactiveList<ElementT>>,
        ): ReactiveList<ElementT> = concatAll(
            lists = of(*lists.toTypedArray()),
        )

        fun <ElementT> concatAll(
            lists: ReactiveList<ReactiveList<ElementT>>,
        ): ReactiveList<ElementT> = ConcatAllReactiveList(
            lists = lists,
        )

        fun <EventT> mergeAll(
            eventStreams: ReactiveList<EventStream<EventT>>,
        ): EventStream<EventT> = DynamicMergeAllEventStream(
            eventStreams = eventStreams,
        )

        fun <ElementT> actuate(
            effectReactiveList: ReactiveList<Effect<ElementT>>,
        ): Effect<ReactiveList<ElementT>> {
            TODO()
        }

        context(momentContext: MomentContext) fun <ElementT, ResultT> looped(
            placeholderReactiveList: ReactiveList<ElementT>,
            block: (ReactiveList<ElementT>) -> Pair<ResultT, ReactiveList<ElementT>>,
        ): ResultT = EventStream.looped { loopedReactiveListSpark: EventStream<ReactiveList<ElementT>> ->
            val diffedReactiveList = ReactiveList.diffDynamic(
                reactiveListCell = loopedReactiveListSpark.holdUnmanaged(placeholderReactiveList),
            )

            val (result, finalReactiveList) = block(diffedReactiveList)

            val reactiveListSpark = EventStream.spark(finalReactiveList)

            Pair(
                result,
                reactiveListSpark,
            )
        }

        fun <ElementT, ResultT> loopedInEffect(
            placeholderReactiveList: ReactiveList<ElementT>,
            block: (ReactiveList<ElementT>) -> Effect<Pair<ResultT, ReactiveList<ElementT>>>,
        ): Effect<ResultT> =
            EventStream.loopedInEffect { loopedReactiveListSpark: EventStream<ReactiveList<ElementT>> ->
                val diffedReactiveList = ReactiveList.diffDynamic(
                    reactiveListCell = loopedReactiveListSpark.holdUnmanaged(placeholderReactiveList),
                )

                block(diffedReactiveList).map { (result, finalReactiveList) ->
                    val reactiveListSpark = EventStream.spark(finalReactiveList)

                    Pair(
                        result,
                        reactiveListSpark,
                    )
                }
            }

        fun <E, R> loopedUnmanaged(
            block: (ReactiveList<E>) -> Pair<R, ReactiveList<E>>,
        ): R {
            val loopedReactiveList = LoopedReactiveList<E>()

            val (result, reactiveList) = block(loopedReactiveList)

            loopedReactiveList.loop(reactiveList)

            return result
        }
    }

    abstract val currentElementsUnmanaged: List<E>

    abstract val elements: Cell<List<E>>

    abstract val newElements: EventStream<List<E>>

    abstract val changes: EventStream<Change<E>>

    abstract fun <Er> map(
        behavior: Behavior = Behavior.Forward,
        transform: (E) -> Er,
    ): ReactiveList<Er>

    context(momentContext: MomentContext)
    fun sampleContent(): List<E> {
        // FIXME: Figure this out
        return currentElementsUnmanaged
    }
}

val <E> ReactiveList<E>.sizeNow: Int
    get() = currentElementsUnmanaged.size

fun <E> ReactiveList<E>.getNow(
    index: Int,
): E = currentElementsUnmanaged[index]

fun <E, Er> ReactiveList<E>.fuseOf(
    transform: (E) -> Cell<Er>,
): ReactiveList<Er> = ReactiveList.fuse(
    cells = this.map(transform = transform),
)

fun <E, Er> ReactiveList<E>.mergeAllOf(
    transform: (E) -> EventStream<Er>,
): EventStream<Er> = ReactiveList.mergeAll(
    eventStreams = this.map(transform = transform),
)

internal fun <E> ReactiveList<E>.copyNow(
    mutableList: MutableList<E>,
) {
    mutableList.clear()
    mutableList.addAll(currentElementsUnmanaged)
}

fun <ElementT> ReactiveList<ElementT>.bind(
    mutableList: MutableList<ElementT>,
): Trigger = Trigger.prepared {
    Actions.mutate {
        copyNow(mutableList = mutableList)
    }

    changes.forEach { change ->
        Actions.mutate {
            change.applyTo(mutableList = mutableList)
        }
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

fun <ElementT, ResultT> ReactiveList<ElementT>.actuateOf(
    transform: (ElementT) -> Effect<ResultT>,
): Effect<ReactiveList<ResultT>> = ReactiveList.actuate(
    this.map {
        transform(it)
    })
