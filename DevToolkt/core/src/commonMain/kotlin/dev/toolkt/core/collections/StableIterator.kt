package dev.toolkt.core.collections

/**
 * An iterator over a stable collection. Allows to sequentially access the elements.
 * Unlike [Iterator], does not have its own internal mutable state.
 */
interface StableIterator<out E> {
    /**
     * Returns the current element in the iteration.
     *
     * @throws IllegalStateException if the iterator is invalid
     */
    fun get(): E

    /**
     * Returns the next iterator in the iteration, or `null` if there are no more elements. Does not invalidate other
     * iterators.
     *
     * @throws IllegalStateException if the iterator is invalid
     */
    fun next(): StableIterator<E>?
}
