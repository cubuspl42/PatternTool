package diy.lingerie.frp

import diy.lingerie.utils.empty
import diy.lingerie.utils.iterable.allUniquePairs
import diy.lingerie.utils.iterable.updateRange
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

            init {
                require(!indexRange.isEmpty() || updatedElements.isNotEmpty())
            }
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

    fun <Er> map(
        transform: (E) -> Er,
    ): DynamicList<Er> = MapDynamicList(
        source = this,
        transform = transform,
    )

    fun get(inex: Int): Cell<E?> {
        TODO()
    }
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

