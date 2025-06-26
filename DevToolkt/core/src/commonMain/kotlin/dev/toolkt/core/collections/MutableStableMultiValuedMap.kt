package dev.toolkt.core.collections

/**
 * A mutable multivalued map providing stable handles to its elements.
 */
interface MutableStableMultiValuedMap<K, V> : MutableMultiValuedMap<K, V>, StableMultiValuedMap<K, V>,
    MutableStableAssociativeBag<K, V>
