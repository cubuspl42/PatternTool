package dev.toolkt.core.collections

interface MutableStableBag<E> : MutableBag<E>, MutableStableCollection<E> {
    /**
     * Adds the specified element to the bag in exchange for a handle.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the added element
     */
    override fun addEx(element: E): StableCollection.Handle<E>
}
