package dev.toolkt.core.collections

interface StableCollection<out E> : Collection<E> {
    interface Handle<E>

    /**
     * A sequence of handles to the elements of this collection, in the order
     * defined by the collection (potentially not a meaningful order).
     */
    val handles: Sequence<Handle<@UnsafeVariance E>>

    /**
     * Returns a handle to any instance of the given [element].
     * Guarantees linear time complexity or better.
     *
     * @return the handle to the element or `null` if the collection does not contain such element
     */
    fun find(
        element: @UnsafeVariance E,
    ): Handle<@UnsafeVariance E>?

    /**
     * Returns the element corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    fun getVia(
        handle: Handle<@UnsafeVariance E>,
    ): E
}
