package dev.toolkt.core.collections

/**
 * A collection associating a set of keys with a set of values in a one-to-many relation,
 * without focusing on lookup optimization.
 */
interface AssociativeBag<K, out V> : Bag<Map.Entry<K, V>>, AssociativeCollection<K, V>
