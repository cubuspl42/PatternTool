package dev.toolkt.core.collections

import dev.toolkt.core.platform.PlatformWeakReference

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

        fun <K, V> newFromStableBag(
            entryBag: MutableStableBag<Map.Entry<K, V>>,
        ): MutableStableMultiValuedMap<K, V> = TODO()

        fun <K: Any, V> newWeakFromStableBag(
            weakEntryBag: MutableStableBag<Map.Entry<PlatformWeakReference<K>, V>>,
        ): MutableStableMultiValuedMap<K, V> = TODO()
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
