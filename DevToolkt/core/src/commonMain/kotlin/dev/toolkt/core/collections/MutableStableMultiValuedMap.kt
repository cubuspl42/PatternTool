package dev.toolkt.core.collections

/**
 * A mutable multivalued map providing stable handles to its elements.
 */
interface MutableStableMultiValuedMap<K, V> : StableMultiValuedMap<K, V>, MutableStableAssociativeCollection<K, V>,
    MutableMultiValuedMap<K, V> {
    companion object {
        fun <K, V> newFromStableMap(
            bucketMap: MutableStableMap<K, MutableStableBag<V>>,
        ): MutableStableMultiValuedMap<K, V> = StableMapBackedMultiValuedMap(
            bucketMap = bucketMap,
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableStableWeakMultiValuedMapOf(): MutableStableMultiValuedMap<K, V> =
    MutableStableMultiValuedMap.newFromStableMap(
        bucketMap = mutableStableWeakMapOf(),
    )

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableStableWeakMapOf(): MutableStableMap<K, V> =
    TODO("Implement mutable stable weak map")
