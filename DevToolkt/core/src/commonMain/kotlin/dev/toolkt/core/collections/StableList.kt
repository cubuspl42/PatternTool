package dev.toolkt.core.collections

/**
 * A read-only list providing stable handles to its elements.
 */
interface StableList<out E> : List<E> {
    interface Handle<E>

    fun resolve(
        index: Int,
    ): Handle<@UnsafeVariance E>?

    /**
     * Returns the element corresponding to the given handle.
     */
    fun getVia(
        handle: Handle<@UnsafeVariance E>,
    ): E

    /**
     * Returns the index of the element corresponding to the given handle in the list.
     */
    fun indexOfVia(
        handle: Handle<@UnsafeVariance E>,
    ): Int
}
