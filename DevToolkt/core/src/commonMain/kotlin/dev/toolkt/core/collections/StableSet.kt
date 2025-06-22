package dev.toolkt.core.collections

/**
 * A read-only set providing stable handles to its elements.
 */
interface StableSet<E> : Set<E> {
    interface Handle<E>

    fun resolve(
        element: E,
    ): Handle<E>?

    /**
     * Returns the element corresponding to the given handle.
     */
    fun getVia(
        handle: Handle<E>,
    ): E
}
