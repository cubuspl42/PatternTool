package dev.toolkt.core.collections

/**
 * A mutable map providing stable handles to its elements.
 */
interface MutableStableMap<K, V> : MutableMap<K, V>, MutableStableAssociativeCollection<K, V>, StableMap<K, V>

fun <K, V> MutableStableMap<K, V>.addEx(
    key: K,
    value: V,
): EntryHandle<K, V>? = addEx(
    element = MapEntry(
        key = key,
        value = value,
    ),
)
