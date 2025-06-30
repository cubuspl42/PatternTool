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
     * Instructs on how to proceed with the given [payload].
     */
    fun instruct(
        payload: PayloadT,
    ): GuideInstruction
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
        GuideInstruction.Stop -> return location

        is GuideInstruction.Turn -> {
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
