package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.getMinimalDescendant

internal class RedBlackTreeIterator<PayloadT>(
    private val tree: RedBlackTree<PayloadT>,
) : HandleIterator<PayloadT, BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>>(
    firstElementHandle = tree.getMinimalDescendant(),
) {
    override fun resolve(
        handle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
    ): PayloadT = tree.getPayload(nodeHandle = handle)

    override fun getNext(
        handle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
    ): BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>? = tree.getInOrderNeighbour(
        nodeHandle = handle,
        side = BinaryTree.Side.Right,
    )

    override fun remove(
        handle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
    ) {
        tree.remove(nodeHandle = handle)
    }
}
