package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableSet.Handle
import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.find
import dev.toolkt.core.data_structures.binary_tree.getMinimalDescendant
import kotlin.jvm.JvmInline

class MutableTreeSet<E : Comparable<E>> internal constructor() : AbstractMutableSet<E>(), MutableStableSet<E> {
    @JvmInline
    internal value class TreeSetHandle<E> internal constructor(
        internal val nodeHandle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
    ) : Handle<E>

    private class TreeSetIterator<E : Comparable<E>>(
        private val tree: RedBlackTree<E>,
    ) : HandleIterator<E, BinaryTree.NodeHandle<E, RedBlackTree.Color>>(
        firstElementHandle = tree.getMinimalDescendant(),
    ) {
        override fun resolve(
            handle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
        ): E = tree.getPayload(nodeHandle = handle)

        override fun getNext(
            handle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
        ): BinaryTree.NodeHandle<E, RedBlackTree.Color>? = tree.getInOrderNeighbour(
            nodeHandle = handle,
            side = BinaryTree.Side.Right,
        )

        override fun remove(
            handle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
        ) {
            tree.remove(nodeHandle = handle)
        }
    }

    private val tree = RedBlackTree<E>()

    override val size: Int
        get() = tree.size

    override fun iterator(): MutableIterator<E> = TreeSetIterator(
        tree = tree,
    )

    override fun resolve(element: E): StableSet.Handle<E>? {
        val location = tree.find(payload = element)
        val nodeHandle = tree.resolve(location = location) ?: return null
        return nodeHandle.pack()
    }

    override fun getVia(handle: StableSet.Handle<E>): E {
        val nodeHandle = handle.unpack()
        return tree.getPayload(nodeHandle = nodeHandle)
    }

    override fun add(
        element: E,
    ): Boolean = addEx(element) != null

    override fun addEx(element: E): StableSet.Handle<E>? {
        val location = tree.find(element)

        when {
            tree.resolve(location = location) == null -> {
                val nodeHandle = tree.insert(
                    location = location,
                    payload = element,
                )

                return nodeHandle.pack()
            }

            else -> {
                return null
            }
        }
    }

    override fun remove(
        element: E,
    ): Boolean {
        val handle = resolve(element = element) ?: return false

        removeVia(handle = handle)

        return true
    }

    override fun removeVia(handle: StableSet.Handle<E>): E {
        val nodeHandle = handle.unpack()

        val removedElement = tree.getPayload(nodeHandle = nodeHandle)

        tree.remove(nodeHandle = nodeHandle)

        return removedElement
    }

    override fun contains(element: E): Boolean {
        val location = tree.find(element)
        return tree.resolve(location = location) != null
    }

}

fun <E : Comparable<E>> mutableTreeSetOf(
    vararg elements: E,
): MutableTreeSet<E> {
    val set = MutableTreeSet<E>()

    for (element in elements) {
        set.add(element)
    }

    return set
}

private fun <E> StableSet.Handle<E>.unpack(): BinaryTree.NodeHandle<E, RedBlackTree.Color> {
    this as? MutableTreeSet.TreeSetHandle<E> ?: throw IllegalArgumentException(
        "Handle is not a TreeSetHandle: $this"
    )

    return this.nodeHandle
}

private fun <E> BinaryTree.NodeHandle<E, RedBlackTree.Color>.pack(): StableSet.Handle<E> = MutableTreeSet.TreeSetHandle(
    nodeHandle = this,
)
