package dev.toolkt.core.collections

/**
 * A read-only map providing stable handles to its entries.
 */
interface StableMap<K, out V> : Map<K, V> {
    interface Handle<K, V>

    /**
     * Returns a handle to the entry corresponding to the given key.
     */
    fun resolve(
        key: K,
    ): Handle<K, @UnsafeVariance V>?

    /**
     * Returns the value corresponding to the given handle.
     */
    fun getVia(
        handle: Handle<K, @UnsafeVariance V>,
    ): V

    /**
     * Returns the entry corresponding to the given handle.
     */
    fun getEntryVia(
        handle: Handle<K, @UnsafeVariance V>,
    ): Map.Entry<K, V>
}
