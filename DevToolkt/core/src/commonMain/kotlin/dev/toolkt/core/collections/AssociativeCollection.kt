package dev.toolkt.core.collections

/**
 * A collection associating a set of keys with a set of values in a possibly
 * many-to-many relation.
 */
interface AssociativeCollection<K, out V> : Collection<Map.Entry<K, V>> {
    /**
     * Returns true if this map contains at least one mapping for the specified key.
     *
     * Guarantees linear time complexity or better.
     */
    fun containsKey(key: K): Boolean

    /**
     * Gets all values associated with the specified key.
     * Guarantees linear time complexity or better.
     */
    fun getAll(key: K): Collection<V>
}

/**
 * Checks whether the collection contains at least one mapping for the specified value.
 */
fun <K, V> AssociativeCollection<K, V>.containsValue(
    value: @UnsafeVariance V,
): Boolean = this.any { entry ->
    entry.value == value
}
