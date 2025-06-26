package dev.toolkt.core.collections

class MapBackedMultiValuedMap<K, V>(
    // TODO: Switch to a mutable collection for the buckets!
    private val backingMap: MutableMap<K, MutableSet<V>>,
) : AbstractMutableCollection<Map.Entry<K, V>>(), MutableMultiValuedMap<K, V> {
    // TODO: Figure out if caching breaks the contract (do we claim ownership?)
    private var cachedSize: Int = backingMap.values.sumOf { it.size }

    override fun clear() {
        backingMap.clear()

        cachedSize = 0
    }

    override fun remove(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = backingMap[key] ?: return false

        if (bucket.isEmpty()) {
            throw AssertionError("Buckets aren't supposed to be empty")
        }

        val wasRemoved = bucket.remove(value)

        if (wasRemoved) {
            cachedSize -= 1
        }

        if (bucket.isEmpty()) {
            val removedBucket = backingMap.remove(key)

            if (removedBucket == null) {
                throw AssertionError("The bucket wasn't successfully removed")
            }
        }

        return wasRemoved
    }

    override fun asMap(): Map<K, Collection<V>> = backingMap

    override fun containsKey(
        key: K,
    ): Boolean = backingMap.containsKey(key)

    override fun getAll(
        key: K,
    ): Collection<V> = backingMap[key] ?: emptySet()

    override fun isEmpty(): Boolean = backingMap.isEmpty()

    override val keys: Set<K>
        get() = backingMap.keys

    override val size: Int
        get() = cachedSize

    override fun iterator(): MutableIterator<Map.Entry<K, V>> = backingMap.asSequence().flatMap { (key, bucket) ->
        bucket.asSequence().map { value ->
            MapEntry(key, value)
        }
    }.iterator().forceMutable()

    override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = backingMap.getOrPut(key) { mutableSetOf() }

        val wasAdded = bucket.add(value)

        if (wasAdded) {
            cachedSize += 1
        }

        return wasAdded
    }

    override val values: Collection<V>
        get() = backingMap.values.flatten()
}
