package dev.toolkt.core.collections

/**
 * An associative bag, i.e. an associative collection which that allows a key
 * to correspond to multiple values.
 */
interface AssociativeBag<K, V> : Bag<Map.Entry<K, V>>, AssociativeCollection<K, V>
