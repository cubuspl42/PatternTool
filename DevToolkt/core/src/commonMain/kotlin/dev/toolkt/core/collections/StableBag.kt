package dev.toolkt.core.collections

/**
 * A read-only bag providing stable handles to its elements. In fact, any useful implementation of a bag should provide
 * stable handles.
 */
interface StableBag<E> : Bag<E> {
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
