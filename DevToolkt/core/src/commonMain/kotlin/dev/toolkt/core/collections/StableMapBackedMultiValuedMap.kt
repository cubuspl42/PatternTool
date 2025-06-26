package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

class StableMapBackedMultiValuedMap<K, V>(
    private val buckets: MutableStableMap<K, MutableStableBag<V>>,
) : AbstractMutableCollection<Map.Entry<K, V>>(), MutableStableMultiValuedMap<K, V> {
    internal data class HandleImpl<K, V>(
        val bucketHandle: BucketHandle<K, V>,
        val valueHandle: Handle<V>,
    ) : EntryHandle<K, V>

    companion object {
        private fun <K, V> pack(
            bucketHandle: BucketHandle<K, V>,
            valueHandle: Handle<V>,
        ): EntryHandle<K, V> = HandleImpl(
            bucketHandle = bucketHandle,
            valueHandle = valueHandle,
        )
    }

    override fun clear() {
        buckets.clear()
    }

    override fun remove(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = buckets[key] ?: return false

        if (bucket.isEmpty()) {
            throw AssertionError("Buckets aren't supposed to be empty")
        }

        val wasRemoved = bucket.remove(value)

        if (bucket.isEmpty()) {
            val removedBucket = buckets.remove(key)

            if (removedBucket == null) {
                throw AssertionError("The bucket wasn't successfully removed")
            }
        }

        return wasRemoved
    }

    override fun asMap(): Map<K, Collection<V>> = buckets

    override fun containsKey(
        key: K,
    ): Boolean = buckets.containsKey(key)

    override fun getAll(
        key: K,
    ): Collection<V> = buckets[key] ?: emptySet()

    override fun isEmpty(): Boolean = buckets.isEmpty()

    override val keys: Set<K>
        get() = buckets.keys

    override val size: Int
        get() = buckets.values.sumOf { it.size }

    override fun iterator(): MutableIterator<Map.Entry<K, V>> {
        val bucketMap: Map<K, Collection<V>> = buckets

        return bucketMap.asSequence().flatMap { (key, bucket) ->
            bucket.asSequence().map { value ->
                MapEntry(key, value)
            }
        }.iterator().forceMutable()
    }

    override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = getFreshBucket(key = key)

        return bucket.add(value)
    }

    override val values: Collection<V>
        get() = buckets.values.flatten()

    override fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, V>> {
        val bucketHandle: BucketHandle<K, V> = buckets.resolve(key = key) ?: return emptyList()
        val bucket = buckets.getValueVia(handle = bucketHandle)

        return bucket.handles.map { valueHandle ->
            StableMapBackedMultiValuedMap.pack(
                bucketHandle = bucketHandle,
                valueHandle = valueHandle,
            )
        }.toList()
    }

    override val handles: Sequence<EntryHandle<K, V>>
        get() = buckets.handles.flatMap { bucketHandle ->
            val bucket = buckets.getValueVia(handle = bucketHandle)

            bucket.handles.map { valueHandle ->
                pack(bucketHandle, valueHandle)
            }
        }

    override fun getVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V> {
        val (key, _, value) = handle.unpackFully()

        return MapEntry(
            key = key,
            value = value,
        )
    }

    override fun addEx(element: Map.Entry<K, V>): EntryHandle<K, V> {
        val (key, value) = element

        // TODO: Get or put ex (with handle)
        val bucket = getFreshBucket(key = key)
        val bucketHandle: BucketHandle<K, V> = TODO()

        val valueHandle = bucket.addEx(value)

        return StableMapBackedMultiValuedMap.pack(
            bucketHandle = bucketHandle,
            valueHandle = valueHandle,
        )
    }

    override fun removeVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V> {
        val handleImpl = handle.unpack()
        val bucketHandle = handleImpl.bucketHandle
        val valueHandle = handleImpl.valueHandle

        val (key, bucket) = buckets.getVia(bucketHandle)

        val removedValue = bucket.removeVia(handle = valueHandle)

        if (bucket.isEmpty()) {
            buckets.removeVia(handle = bucketHandle)
        }

        return MapEntry(
            key = key,
            value = removedValue,
        )
    }

    private fun getFreshBucket(
        key: K,
    ): MutableStableBag<V> = buckets.getOrPut(key) {
        mutableStableListOf()
    }


    private fun EntryHandle<K, V>.unpackFully(): UnpackedHandle<K, V> {
        val handleImpl = this.unpack()

        val bucketEntry = buckets.getVia(handle = handleImpl.bucketHandle)
        val key = bucketEntry.key


        val bucket = bucketEntry.value
        val value = bucket.getVia(handle = handleImpl.valueHandle)

        return UnpackedHandle(
            key = key,
            bucket = bucket,
            value = value,
        )
    }
}

private typealias BucketHandle<K, V> = EntryHandle<K, MutableStableBag<V>>

private data class UnpackedHandle<K, V>(
    val key: K,
    val bucket: MutableStableBag<V>,
    val value: V,
)

private fun <K, V> EntryHandle<K, V>.unpack(): StableMapBackedMultiValuedMap.HandleImpl<K, V> =
    this as? StableMapBackedMultiValuedMap.HandleImpl<K, V> ?: throw IllegalArgumentException(
        "Handle is not a StableMapBackedMultiValuedMap.HandleImpl: $this"
    )
