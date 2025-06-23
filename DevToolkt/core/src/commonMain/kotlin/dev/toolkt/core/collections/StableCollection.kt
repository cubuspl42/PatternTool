package dev.toolkt.core.collections

interface StableCollection<E> : Collection<E> {
    interface Handle<E>

    /**
     * Returns a handle to the element corresponding to the given element.
     * Guarantees linear time complexity or better.
     */
    fun resolve(
        element: E,
    ): Handle<E>?

    /**
     * Returns the element corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    fun getVia(
        handle: Handle<E>,
    ): E
}
