package dev.toolkt.core.collections

interface MutableStableAssociativeBag<K, V> : MutableStableBag<Map.Entry<K, V>>, MutableAssociativeBag<K, V> {
    companion object {
        fun <K, V : Any> create(): MutableStableAssociativeBag<K, V> = backed(
            // TODO: Can be replaced with a linked list
            entries = MutableTreeList(),
        )

        fun <K, V : Any> backed(
            entries: MutableStableBag<Map.Entry<K, V>>,
        ): MutableStableAssociativeBag<K, V> = StableBagBackedStableAssociativeBag(
            entries = entries,
        )
    }
}

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
}
