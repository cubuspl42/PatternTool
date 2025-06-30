package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree

/**
 * An instruction on how to proceed with the search in a binary tree.
 */
sealed class GuideInstruction {
    /**
     * An instruction to turn to (recurse to) a side of the tree.
     */
    data class Turn(
        /**
         * The side of the tree to turn to
         */
        val side: BinaryTree.Side,
    ) : GuideInstruction()

    /**
     * An instruction to stop, meaning that the payload has been found
     */
    data object Stop : GuideInstruction()

    companion object {
        fun <T : Comparable<T>> comparing(
            expected: T,
            actual: T,
        ): GuideInstruction {
            val result = expected.compareTo(actual)

            return when {
                result == 0 -> Stop

                else -> Turn(
                    side = when {
                        result < 0 -> BinaryTree.Side.Left
                        else -> BinaryTree.Side.Right
                    },
                )
            }
        }
    }
}
