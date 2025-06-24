package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

interface MutableStableCollection<E> : MutableSet<E>, StableSet<E> {
    /**
     * Adds the specified element to the collection in exchange for a handle.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the added element or `null` if the element is already present.
     */
    fun addEx(
        element: E,
    ): Handle<E>?

    /**
     * Removes the element corresponding to the given handle from the collection.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the element that has been removed.
     */
    fun removeVia(
        handle: Handle<E>,
    ): E
}
