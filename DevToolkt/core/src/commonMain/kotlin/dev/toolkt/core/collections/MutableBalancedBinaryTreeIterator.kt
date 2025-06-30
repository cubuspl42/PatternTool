package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.getMinimalDescendant

internal class MutableBalancedBinaryTreeIterator<PayloadT, ColorT>(
    private val tree: MutableBalancedBinaryTree<PayloadT, ColorT>,
) : HandleIterator<PayloadT, BinaryTree.NodeHandle<PayloadT, ColorT>>(
    firstElementHandle = tree.getMinimalDescendant(),
) {
    override fun resolve(
        handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): PayloadT = tree.getPayload(nodeHandle = handle)

    override fun getNext(
        handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>? = tree.getInOrderNeighbour(
        nodeHandle = handle,
        side = BinaryTree.Side.Right,
    )

    override fun remove(
        handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        tree.remove(nodeHandle = handle)
    }
}
