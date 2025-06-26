package dev.toolkt.core.collections

/**
 * A collection associating a set of keys with a set of values in a possibly
 * one-to-many relation.
 */
interface AssociativeCollection<K, out V> : Collection<Map.Entry<K, V>> {
    /**
     * Gets all values associated with the specified key.
     * Guarantees linear time complexity or better.
     */
    fun getAll(key: K): Collection<V>
}
