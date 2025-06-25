package dev.toolkt.core.collections

/**
 * A read-only map providing stable handles to its entries.
 */
interface StableMap<K, out V> : StableAssociativeCollection<K, V>, Map<K, V>
