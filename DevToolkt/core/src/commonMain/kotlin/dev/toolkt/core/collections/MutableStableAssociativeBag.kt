package dev.toolkt.core.collections

interface MutableStableAssociativeBag<K, V> : MutableStableBag<Map.Entry<K, V>>,
    MutableStableAssociativeCollection<K, V>, StableAssociativeBag<K, V> {
    companion object {
        fun <K, V : Any> newFromBag(
            entryBag: MutableStableBag<Map.Entry<K, V>>,
        ): MutableStableAssociativeBag<K, V> = StableBagBackedStableAssociativeBag(
            entries = entryBag,
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableStableAssociativeBagOf(
    vararg elements: Map.Entry<K, V>,
): MutableStableAssociativeBag<K, V> = MutableStableAssociativeBag.newFromBag(
    entryBag = mutableStableBagOf(*elements),
)

private class StableBagBackedStableAssociativeBag<K, V : Any>(
    private val entries: MutableStableBag<Map.Entry<K, V>>,
) : MutableStableAssociativeBag<K, V>, MutableStableBag<Map.Entry<K, V>> by entries {
    override fun containsKey(key: K): Boolean = entries.any { entry ->
        entry.key == key
    }

    override fun getAll(key: K): Collection<V> = entries.mapNotNull {
        when {
            it.key == key -> it.value
            else -> null
        }
    }

    override fun resolveAll(key: K): Collection<EntryHandle<K, V>> = entries.handles.filter { entryHandle ->
        val entry = entries.getVia(entryHandle)
        entry.key == key
    }.toList()

    override fun removeKey(key: K): Boolean = entries.removeAll { entry ->
        entry.key == key
    }
}
