package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableSet.Handle

/**
 * A mutable set providing stable handles to its elements.
 */
interface MutableStableSet<E> : MutableSet<E>, StableSet<E> {
    /**
     * Adds the specified element to the set in exchange for a handle.
     *
     * @return the handle to the added element or `null` if the element is already present.
     */
    fun addEx(
        element: E,
    ): Handle<E>?

    /**
     * Removes the element corresponding to the given handle from the set.
     *
     * @return the element that has been removed.
     */
    fun removeVia(
        handle: Handle<E>,
    ): E
}
