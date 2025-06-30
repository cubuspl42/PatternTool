package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getChildLocation

/**
 * A guide that can be used to find a specific location in the binary tree. A
 * guide (typically) takes some assumptions about the structure of the tree,
 * i.e. how the node's index in the order corresponds to its payload.
 */
interface Guide<in PayloadT> {
    /**
     * An instruction on how to proceed with the search in a binary tree.
     */
    sealed class Instruction {
        /**
         * An instruction to turn to (recurse to) a side of the tree.
         */
        data class Turn(
            /**
             * The side of the tree to turn to
             */
            val side: BinaryTree.Side,
        ) : Instruction()

        /**
         * An instruction to stop, meaning that the payload has been found
         */
        data object Stop : Instruction()

        companion object {
            fun <T : Comparable<T>> comparing(
                expected: T,
                actual: T,
            ): Instruction {
                val result = expected.compareTo(actual)

                return when {
                    result == 0 -> Stop

                    else -> Instruction.Turn(
                        side = when {
                            result < 0 -> BinaryTree.Side.Left
                            else -> BinaryTree.Side.Right
                        },
                    )
                }
            }
        }
    }

    /**
     * Instructs on how to proceed with the given [payload].
     */
    fun instruct(
        payload: PayloadT,
    ): Instruction
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.findLocationGuided(
    guide: Guide<PayloadT>,
): BinaryTree.Location<PayloadT, ColorT> = this.findLocationGuided(
    location = BinaryTree.RootLocation,
    guide = guide,
)

private tailrec fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.findLocationGuided(
    location: BinaryTree.Location<PayloadT, ColorT>,
    guide: Guide<PayloadT>,
): BinaryTree.Location<PayloadT, ColorT> {
    val nodeHandle = resolve(
        location = location,
    ) ?: return location

    val payload = getPayload(
        nodeHandle = nodeHandle,
    )

    val instruction = guide.instruct(
        payload = payload,
    )

    when (instruction) {
        Guide.Instruction.Stop -> return location

        is Guide.Instruction.Turn -> {
            val childLocation = nodeHandle.getChildLocation(
                side = instruction.side,
            )

            return findLocationGuided(
                location = childLocation,
                guide = guide,
            )
        }
    }
}
