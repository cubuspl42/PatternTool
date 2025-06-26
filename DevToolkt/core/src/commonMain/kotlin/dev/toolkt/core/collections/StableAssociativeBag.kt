package dev.toolkt.core.collections

/**
 * An associative bag providing stable handles to its entries.
 */
interface StableAssociativeBag<K, out V> : StableBag<Map.Entry<K, V>>, StableAssociativeCollection<K, V>
