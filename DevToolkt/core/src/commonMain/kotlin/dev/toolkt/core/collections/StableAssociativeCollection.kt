package dev.toolkt.core.collections

typealias EntryHandle<K, V> = StableCollection.Handle<Map.Entry<K, V>>

/**
 * A read-only associative collection providing stable handles to its entries.
 */
interface StableAssociativeCollection<K, out V> : StableCollection<Map.Entry<K, V>>, AssociativeCollection<K, V> {
    /**
     * Returns handles to the entries corresponding to the given key.
     * Guarantees linear time complexity or better.
     */
    fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, @UnsafeVariance V>>
}
