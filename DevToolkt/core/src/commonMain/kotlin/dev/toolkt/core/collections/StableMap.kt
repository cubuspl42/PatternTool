package dev.toolkt.core.collections

/**
 * A read-only map providing stable handles to its entries.
 */
interface StableMap<K, out V> : StableAssociativeCollection<K, V>, StableSet<Map.Entry<K, V>>, Map<K, V> {
    /**
     * Returns a handle to the entry corresponding to the given key.
     * Guarantees linear time complexity or better.
     */
    fun resolve(
        key: K,
    ): EntryHandle<K, @UnsafeVariance V>?
}

/**
 * Returns the value corresponding to the given handle.
 * Guarantees constant time complexity.
 * TODO: Return null if the entry was removed via another handle?
 */
fun <K, V: Any> StableMap<K, V>.getValueVia(
    handle: EntryHandle<K, @UnsafeVariance V>,
): V {
    val entry = getVia(handle = handle)
    return entry.value
}
