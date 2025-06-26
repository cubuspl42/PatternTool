package dev.toolkt.core.collections

import dev.toolkt.core.platform.mutableWeakMapOf

interface MutableMultiValuedMap<K, V> : MultiValuedMap<K, V>, MutableAssociativeCollection<K, V> {
    companion object {
        fun <K, V> newFromMap(
            backingMap: MutableMap<K, MutableSet<V>>,
        ): MutableMultiValuedMap<K, V> = MapBackedMultiValuedMap(
            backingMap = backingMap,
        )

        fun <K, V> new(): MutableMultiValuedMap<K, V> = newFromMap(mutableMapOf())
    }
}

/**
 * Adds a key-value mapping to this multivalued map.
 */
fun <K, V> MutableMultiValuedMap<K, V>.put(
    key: K,
    value: V,
) {
    this.add(key, value)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> mutableMultiValuedMapOf(
    vararg pairs: Pair<K, V>,
): MutableMultiValuedMap<K, V> = MutableMultiValuedMap.newFromMap(
    backingMap = pairs.groupBy { (key, _) -> key }.mapValues { (_, keyPairs) ->
        keyPairs.map { (_, value) -> value }.toMutableSet()
    }.toMutableMap(),
)

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableWeakMultiValuedMapOf(): MutableMultiValuedMap<K, V> =
    MutableMultiValuedMap.newFromMap(
        backingMap = mutableWeakMapOf(),
    )
