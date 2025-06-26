package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

interface MutableStableBag<E> : MutableBag<E>, StableBag<E>, MutableStableCollection<E> {
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
     * Adds the specified element to the bag in exchange for a handle.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the added element
     */
    override fun addEx(element: E): Handle<E>
}
