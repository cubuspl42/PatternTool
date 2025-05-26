package diy.lingerie.frp.dynamic_list

import dev.toolkt.core.iterable.allUniquePairs
import diy.lingerie.frp.cell.Cell
import diy.lingerie.frp.event_stream.EventStream
import diy.lingerie.utils.empty
import dev.toolkt.core.iterable.updateRange
import diy.lingerie.utils.overlaps

abstract class DynamicList<out E> {
    data class Change<out E>(
        val updates: Set<Update<E>>,
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

                fun <E> remove(
                    indexRange: IntRange,
                ): Update<E> = Update(
                    indexRange = indexRange,
                    updatedElements = emptyList(),
                )

                fun <E> insert(
                    index: Int,
                    newElements: List<E>,
                ): Update<E> = Update(
                    indexRange = IntRange.empty(index),
                    updatedElements = newElements,
                )
            }

            fun toChange(): Change<E> = Change(
                updates = setOf(this),
            )

            init {
                require(!indexRange.isEmpty() || updatedElements.isNotEmpty())
            }
        }

        companion object {
            fun <E> single(
                update: Update<E>,
            ): Change<E> = Change(
                updates = setOf(update),
            )
        }

        val updatesInOrder: List<Update<E>>
            get() = updates.sortedBy { it.indexRange.first }

        init {
            updates.allUniquePairs().none { (updateA, updateB) ->
                updateA.indexRange.overlaps(updateB.indexRange)
            }
        }
    }

    object Empty : DynamicList<Nothing>() {
        override val currentElements: List<Nothing> = emptyList()

        override val changes: EventStream<Change<Nothing>> = EventStream.Never

        override fun <Er> map(
            transform: (Nothing) -> Er,
        ): DynamicList<Er> = Empty

        override fun <T : Any> pipe(
            target: T,
            mutableList: MutableList<*>,
        ) {
            mutableList.clear()
        }
    }

    companion object {
        fun <E> of(
            vararg children: E,
        ): DynamicList<E> = ConstDynamicList(
            constElements = children.toList(),
        )
    }

    abstract val currentElements: List<E>

    abstract val changes: EventStream<Change<E>>

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): DynamicList<Er>

    fun get(inex: Int): Cell<E?> {
        TODO()
    }

    abstract fun <T : Any> pipe(
        target: T,
        mutableList: MutableList<in E>,
    )
}

internal fun <E> DynamicList<E>.copyNow(
    mutableList: MutableList<E>,
) {
    mutableList.clear()
    mutableList.addAll(currentElements)
}

fun <E> DynamicList.Change.Update<E>.applyTo(
    mutableList: MutableList<E>,
) {
    mutableList.updateRange(
        indexRange = indexRange,
        elements = updatedElements,
    )
}

fun <E> DynamicList.Change<E>.applyTo(
    mutableList: MutableList<E>,
) {
    updatesInOrder.reversed().forEach { update ->
        update.applyTo(mutableList = mutableList)
    }
}

