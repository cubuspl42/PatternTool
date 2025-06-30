package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.errors.assert
import dev.toolkt.core.iterable.uncons

abstract class AbstractBalancedBinaryTree<PayloadT, ColorT>(
    protected val internalTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
) : MutableBalancedBinaryTree<PayloadT, ColorT>, BinaryTree<PayloadT, ColorT> by internalTree {
    data class RebalanceResult<PayloadT, ColorT>(
        /**
         * The highest location that was reached during rebalancing
         */
        val finalLocation: BinaryTree.Location<PayloadT, ColorT>,
        /**
         * The number of tree levels that were affected by the rebalancing, relative to the node that was pointed as
         * its starting point.
         */
        val retractionHeight: Int,
    )

    override fun setPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        payload: PayloadT,
    ) {
        internalTree.setPayload(
            nodeHandle = nodeHandle,
            payload = payload,
        )
    }

    final override fun insert(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val insertedNodeHandle = internalTree.attach(
            location = location,
            payload = payload,
            color = defaultColor,
        )

        // Rebalance the tree after insertion
        rebalanceAfterAttach(
            putNodeHandle = insertedNodeHandle,
        )

        return insertedNodeHandle
    }

    final override fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.Location<PayloadT, ColorT> {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        val swapResult = if (leftChildHandle != null && rightChildHandle != null) {
            // If the node has two children, we can't directly remove it, but we can swap it with its
            // successor

            internalTree.swap(
                nodeHandle = nodeHandle,
                side = BinaryTree.Side.Right,
            )

            // After the swap, the node has at most one child (as the successor
            // was guaranteed to have at most one child)
        } else null

        val rebalanceResult = removeDirectly(nodeHandle = nodeHandle)

        return when (swapResult) {
            null -> rebalanceResult.finalLocation

            else -> when {
                rebalanceResult.retractionHeight > swapResult.neighbourDepth -> rebalanceResult.finalLocation

                else -> locate(swapResult.neighbourHandle)
            }
        }
    }

    /**
     * Remove the node directly, which is possible only if it has at most one child.
     */
    private fun removeDirectly(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): RebalanceResult<PayloadT, ColorT> {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        assert(leftChildHandle == null || rightChildHandle == null) {
            "The node must have at most one child, but has both left and right children"
        }

        val singleChildHandle = leftChildHandle ?: rightChildHandle

        return when (singleChildHandle) {
            null -> {
                val relativeLocation = internalTree.locateRelatively(nodeHandle = nodeHandle)
                val leafColor = internalTree.getColor(nodeHandle = nodeHandle)

                internalTree.cutOff(leafHandle = nodeHandle)

                when {
                    relativeLocation != null -> rebalanceAfterCutOff(
                        cutOffLeafLocation = relativeLocation,
                        cutOffLeafColor = leafColor,
                    )

                    // If we cut off the root, there's no need to rebalance
                    else -> RebalanceResult(
                        retractionHeight = 0,
                        finalLocation = BinaryTree.RootLocation,
                    )
                }
            }

            else -> {
                val elevatedNodeHandle = internalTree.collapse(nodeHandle = nodeHandle)

                rebalanceAfterCollapse(
                    elevatedNodeHandle = elevatedNodeHandle,
                )
            }
        }
    }

    abstract val defaultColor: ColorT

    abstract fun rebalanceAfterAttach(
        putNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): RebalanceResult<PayloadT, ColorT>

    abstract fun rebalanceAfterCutOff(
        cutOffLeafLocation: BinaryTree.RelativeLocation<PayloadT, ColorT>,
        cutOffLeafColor: ColorT,
    ): RebalanceResult<PayloadT, ColorT>

    abstract fun rebalanceAfterCollapse(
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): RebalanceResult<PayloadT, ColorT>
}
