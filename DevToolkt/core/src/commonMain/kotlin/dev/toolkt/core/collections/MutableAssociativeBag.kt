package dev.toolkt.core.collections

interface MutableAssociativeBag<K, V> : MutableBag<Map.Entry<K, V>>, AssociativeBag<K, V>,
    MutableAssociativeCollection<K, V>
