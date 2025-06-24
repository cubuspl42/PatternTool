package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

/**
 * A mutable list providing stable handles to its elements.
 */
interface MutableStableList<E> : MutableList<E>, StableList<E> {
    /**
     * Replaces the element corresponding to the given handle with the specified element. Doesn't invalidate the handle.
     *
     * @return the element previously at the specified position.
     */
    fun setVia(
        handle: Handle<E>,
        element: E,
    ): E

    /**
     * Adds the specified element to the end of this list in exchange for a handle.
     *
     * @return the handle to the added element.
     */
    fun addEx(
        element: E,
    ): Handle<E>

    /**
     * Inserts an element into the list at the specified [index] in exchange for a handle.
     *
     * @return the handle to the added element.
     */
    fun addAtEx(
        index: Int,
        element: E,
    ): Handle<E>

    /**
     * Removes the element corresponding to the given handle from the list.
     *
     * @return the element that has been removed.
     */
    fun removeVia(
        handle: Handle<E>,
    ): E
}
