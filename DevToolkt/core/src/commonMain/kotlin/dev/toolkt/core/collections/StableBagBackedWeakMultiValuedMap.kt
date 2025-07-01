package dev.toolkt.core.collections

import dev.toolkt.core.platform.PlatformWeakReference
import kotlin.jvm.JvmInline

class StableBagBackedWeakMultiValuedMap<K : Any, V>(
    private val entryBag: MutableStableBag<Map.Entry<PlatformWeakReference<K>, V>>,
) : AbstractMutableCollection<Map.Entry<K, V>>(), MutableStableMultiValuedMap<K, V> {
    @JvmInline
    internal value class HandleImpl<K : Any, V>(
        val weakEntryHandle: EntryHandle<PlatformWeakReference<K>, V>,
    ) : EntryHandle<K, V>

    override fun clear() {
        entryBag.clear()
    }

    override fun remove(
        element: Map.Entry<K, V>,
    ): Boolean = entryBag.removeAll {
        it.key.get() == element.key && it.value == element.value
    }

    override fun asMap(): Map<K, Collection<V>> = TODO()

    override fun containsKey(
        key: K,
    ): Boolean = entryBag.any {
        it.key.get() == key
    }

    override fun getAll(
        key: K,
    ): Collection<V> = entryBag.mapNotNull {
        when {
            it.key.get() == key -> it.value
            else -> null
        }
    }

    override fun isEmpty(): Boolean = entryBag.isEmpty()

    override val keys: Set<K>
        get() = entryBag.mapNotNull { it.key.get() }.toSet()

    override val size: Int
        get() = entryBag.size

    override fun iterator(): MutableIterator<Map.Entry<K, V>> {
        TODO()
    }

    override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val weakEntryHandle = entryBag.addEx(
            MapEntry(
                key = PlatformWeakReference(key),
                value = value,
            )
        )

        TODO()
    }

    override val values: Collection<V>
        get() = entryBag.map { it.value }

    override fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, V>> {
        TODO()
    }

    override val handles: Sequence<EntryHandle<K, V>>
        get() = TODO()

    override fun getVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {

        TODO()
    }

    override fun stableIterator(): StableIterator<Map.Entry<K, V>>? {
        TODO("Not yet implemented")
    }

    override fun addEx(
        element: Map.Entry<K, V>,
    ): EntryHandle<K, V> {
        TODO()
    }

    override fun removeVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        TODO()
    }

    override fun removeKey(key: K): Boolean {
        TODO()
    }
}

private typealias WeakEntryHandle<K, V> = EntryHandle<PlatformWeakReference<K>, V>

private fun <K : Any, V> EntryHandle<K, V>.unpack(): WeakEntryHandle<K, V> {
    this as? StableBagBackedWeakMultiValuedMap.HandleImpl<K, V> ?: throw IllegalArgumentException(
        "Handle is not a StableBagBackedMultiValuedMap.HandleImpl: $this"
    )

    return weakEntryHandle
}

private fun <K : Any, V> WeakEntryHandle<K, V>.pack(): StableBagBackedWeakMultiValuedMap.HandleImpl<K, V> {
    return StableBagBackedWeakMultiValuedMap.HandleImpl(
        weakEntryHandle = this,
    )
}
