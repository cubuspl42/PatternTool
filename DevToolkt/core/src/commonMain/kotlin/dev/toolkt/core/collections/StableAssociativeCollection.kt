package dev.toolkt.core.collections

typealias EntryHandle<K, V> = StableCollection.Handle<Map.Entry<K, V>>

/**
 * A read-only associative collection providing stable handles to its entries.
 */
interface StableAssociativeCollection<K, out V> {
    /**
     * Returns a handle to the entry corresponding to the given key.
     * Guarantees linear time complexity or better.
     */
    fun resolve(
        key: K,
    ): EntryHandle<K, @UnsafeVariance V>?

    /**
     * Returns the value corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    fun getVia(
        handle: EntryHandle<K, @UnsafeVariance V>,
    ): V

    /**
     * Returns the entry corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    fun getEntryVia(
        handle: EntryHandle<K, @UnsafeVariance V>,
    ): Map.Entry<K, V>
}
