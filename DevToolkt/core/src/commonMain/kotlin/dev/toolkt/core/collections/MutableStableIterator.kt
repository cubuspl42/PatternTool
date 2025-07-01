package dev.toolkt.core.collections

/**
 * An iterator over a mutable stable collection. Provides the ability to remove elements while iterating.
 * Unlike [MutableIterator], does not have its own internal mutable state.
 */
interface MutableStableIterator<E> : StableIterator<E> {
    /**
     * Removes the current element from the underlying collection. Invalidates the iterator. To guarantee
     * undisturbed iteration, `next()` must be called to retrieve the next iterator _before_ calling this method.
     */
    fun remove()
}
