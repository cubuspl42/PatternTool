package dev.toolkt.core.collections

/**
 * A read-only associative collection providing stable handles to its entries.
 */
interface StableAssociativeCollection<K, out V> {
    interface Handle<K, V>

    /**
     * Returns a handle to the entry corresponding to the given key.
     * Guarantees linear time complexity or better.
     */
    fun resolve(
        key: K,
    ): Handle<K, @UnsafeVariance V>?

    /**
     * Returns the value corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    fun getVia(
        handle: Handle<K, @UnsafeVariance V>,
    ): V

    /**
     * Returns the entry corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    fun getEntryVia(
        handle: Handle<K, @UnsafeVariance V>,
    ): Map.Entry<K, V>
}
