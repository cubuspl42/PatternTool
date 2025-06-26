package dev.toolkt.core.collections

/**
 * A collection associating a set of keys with a set of values in a many-to-many relation.
 */
interface AssociativeBag<K, out V> : Bag<Map.Entry<K, V>>, AssociativeCollection<K, V>
