package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

/**
 * A read-only list providing stable handles to its elements.
 */
interface IndexedList<out E> : StableList<E> {
    /**
     * Returns the handle to the element at the specified [index] in the list.
     * Guarantees logarithmic time complexity or better.
     */
    override fun select(
        index: Int,
    ): Handle<@UnsafeVariance E>?

    /**
     * Returns the index of the element corresponding to the given handle in the list.
     * Guarantees logarithmic time complexity or better.
     */
    fun indexOfVia(
        handle: StableCollection.Handle<@UnsafeVariance E>,
    ): Int
}
