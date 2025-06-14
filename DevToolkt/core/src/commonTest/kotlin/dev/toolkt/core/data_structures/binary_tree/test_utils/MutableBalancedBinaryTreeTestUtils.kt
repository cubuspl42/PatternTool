package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree

internal data object BalanceVerificator : HeightVerificator {
    override fun verifySubtreeHeight(
        leftSubtreeHeight: Int,
        rightSubtreeHeight: Int,
    ) {
        val balanceFactor = leftSubtreeHeight - rightSubtreeHeight

        if (balanceFactor < -1 || balanceFactor > 1) {
            throw AssertionError("Unbalanced subtree, balance factor: $balanceFactor")
        }
    }
}

fun <PayloadT: Comparable<PayloadT>, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.insertVerified(
    location: BinaryTree.Location<PayloadT, ColorT>,
    payload: PayloadT,
    colorVerificator: ColorVerificator<ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val insertedNodeHandle = this.insert(
        location = location,
        payload = payload,
    )

    verifyIntegrity(
        heightVerificator = BalanceVerificator,
        colorVerificator = colorVerificator,
    )

    return insertedNodeHandle
}

fun <PayloadT: Comparable<PayloadT>, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.removeVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    colorVerificator: ColorVerificator<ColorT>,
) {
    this.remove(
        nodeHandle = nodeHandle,
    )

    verifyIntegrity(
        heightVerificator = BalanceVerificator,
        colorVerificator = colorVerificator,
    )
}
