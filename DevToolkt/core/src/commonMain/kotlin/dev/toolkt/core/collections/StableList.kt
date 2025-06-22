package dev.toolkt.core.collections

/**
 * A read-only list providing stable handles to its elements.
 */
interface StableList<E> : List<E> {
    interface Handle<E>

    fun resolve(
        index: Int,
    ): Handle<E>?

    /**
     * Returns the element corresponding to the given handle.
     */
    fun getVia(
        handle: Handle<E>,
    ): E

    /**
     * Returns the index of the element corresponding to the given handle in the list.
     */
    fun indexOfVia(
        handle: Handle<E>,
    ): Int
}
