package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableMap.Handle

/**
 * A mutable map providing stable handles to its elements.
 */
interface MutableStableMap<K, V> : MutableMap<K, V>, StableMap<K, V> {
    /**
     * Add the specified entry to the map in exchange for a handle. If an entry with the given key is already present,
     * it does nothing.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the added entry or `null` if the entry with the given key is already present.
     */
    fun addEx(
        key: K,
        value: V,
    ): Handle<K, V>?

    /**
     * Removes the entry corresponding to the given handle from the map.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the entry that has been removed.
     */
    fun removeVia(
        handle: Handle<K, V>,
    ): Map.Entry<K, V>
}
