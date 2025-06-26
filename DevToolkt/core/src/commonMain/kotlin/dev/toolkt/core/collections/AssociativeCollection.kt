package dev.toolkt.core.collections

interface AssociativeCollection<K, out V> : Collection<Map.Entry<K, V>> {
    /**
     * Gets all values associated with the specified key.
     * Guarantees linear time complexity or better.
     */
    fun getAll(key: K): Collection<V>
}
