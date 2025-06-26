package dev.toolkt.core.collections

interface MutableStableAssociativeBag<K, V> : MutableStableBag<Map.Entry<K, V>>, MutableAssociativeBag<K, V>
