package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableAssociativeCollection.Handle
import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.findBy
import kotlin.jvm.JvmInline

class MutableTreeMap<K : Comparable<K>, V> internal constructor() : AbstractMutableMap<K, V>(), MutableStableMap<K, V> {
    internal class MutableMapEntry<K : Comparable<K>, V>(
        override val key: K,
        initialValue: V,
    ) : MutableMap.MutableEntry<K, V> {
        companion object {
            fun <K : Comparable<K>, V> selectKey(
                entry: MutableMapEntry<K, V>,
            ): K = entry.key
        }

        private var mutableValue: V = initialValue

        override val value: V
            get() = mutableValue

        override fun setValue(newValue: V): V {
            val previousValue = mutableValue

            mutableValue = newValue

            return previousValue
        }
    }

    internal class EntrySet<K : Comparable<K>, V>(
        private val entryTree: RedBlackTree<MutableMapEntry<K, V>>,
    ) : AbstractMutableCollection<MutableMap.MutableEntry<K, V>>(), MutableSet<MutableMap.MutableEntry<K, V>> {
        override val size: Int
            get() = entryTree.size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = RedBlackTreeIterator(tree = entryTree)

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
            throw UnsupportedOperationException()
        }
    }

    @JvmInline
    internal value class TreeMapHandle<K : Comparable<K>, V> internal constructor(
        internal val nodeHandle: EntryNodeHandle<K, V>,
    ) : Handle<K, V>

    private val entryTree = RedBlackTree<MutableMapEntry<K, V>>()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = EntrySet(entryTree = entryTree)

    override val size: Int
        get() = entryTree.size

    override fun put(
        key: K,
        value: V,
    ): V? {
        val (location, existingNodeHandle) = findByKey(key = key)

        return when (existingNodeHandle) {
            null -> {
                entryTree.insert(
                    location = location,
                    payload = MutableMapEntry(
                        key = key,
                        initialValue = value,
                    ),
                )

                null
            }

            else -> {
                val existingEntry = entryTree.getPayload(existingNodeHandle)
                val previousValue = existingEntry.value

                existingEntry.setValue(value)

                previousValue
            }
        }
    }

    override fun addEx(
        key: K,
        value: V,
    ): Handle<K, V>? {
        val (location, existingNodeHandle) = findByKey(key = key)

        if (existingNodeHandle != null) {
            return null
        }

        val insertedNodeHandle = entryTree.insert(
            location = location,
            payload = MutableMapEntry(
                key = key,
                initialValue = value,
            ),
        )

        return insertedNodeHandle.pack()
    }

    override fun removeVia(
        handle: Handle<K, V>,
    ): Map.Entry<K, V> {
        val nodeHandle = handle.unpack()
        val removedEntry = entryTree.getPayload(nodeHandle = nodeHandle)

        entryTree.remove(nodeHandle = nodeHandle)

        return removedEntry
    }

    override fun resolve(
        key: K,
    ): Handle<K, V>? {
        val (_, nodeHandle) = findByKey(key = key)
        return nodeHandle?.pack()
    }

    override fun getVia(
        handle: Handle<K, V>,
    ): V {
        val nodeHandle = handle.unpack()
        val entry = entryTree.getPayload(nodeHandle = nodeHandle)
        return entry.value
    }

    override fun getEntryVia(
        handle: Handle<K, V>,
    ): Map.Entry<K, V> {
        val nodeHandle = handle.unpack()
        return entryTree.getPayload(nodeHandle = nodeHandle)
    }

    private fun findByKey(
        key: K,
    ): Pair<EntryLocation<K, V>, EntryNodeHandle<K, V>?> {
        val location = entryTree.findBy(
            key = key,
            selector = MutableMapEntry.Companion::selectKey,
        )

        val existingNodeHandle = entryTree.resolve(
            location = location,
        )

        return Pair(location, existingNodeHandle)
    }
}

fun <K : Comparable<K>, V> mutableTreeMapOf(
    vararg pairs: Pair<K, V>,
): MutableTreeMap<K, V> {
    val map = MutableTreeMap<K, V>()

    for ((key, value) in pairs) {
        map.put(key, value)
    }

    return map
}

private typealias EntryLocation<K, V> = BinaryTree.Location<MutableTreeMap.MutableMapEntry<K, V>, RedBlackTree.Color>

private typealias EntryNodeHandle<K, V> = BinaryTree.NodeHandle<MutableTreeMap.MutableMapEntry<K, V>, RedBlackTree.Color>

private fun <K : Comparable<K>, V> Handle<K, V>.unpack(): BinaryTree.NodeHandle<MutableTreeMap.MutableMapEntry<K, V>, RedBlackTree.Color> {
    this as? MutableTreeMap.TreeMapHandle<K, V> ?: throw IllegalArgumentException(
        "Handle is not a TreeMapHandle: $this"
    )

    return this.nodeHandle
}

private fun <K : Comparable<K>, V> BinaryTree.NodeHandle<MutableTreeMap.MutableMapEntry<K, V>, RedBlackTree.Color>.pack(): Handle<K, V> =
    MutableTreeMap.TreeMapHandle(
        nodeHandle = this,
    )

