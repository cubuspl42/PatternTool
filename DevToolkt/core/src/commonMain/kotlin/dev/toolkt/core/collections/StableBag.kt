package dev.toolkt.core.collections

/**
 * A read-only bag providing stable handles to its elements. In fact, any useful implementation of a bag should provide
 * stable handles.
 */
interface StableBag<E> : StableCollection<E>
