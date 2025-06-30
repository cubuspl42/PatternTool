package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree

fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.find(
    payload: PayloadT,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    guide = IntrinsicOrderGuide(
        locatedPayload = payload,
    ),
)

/**
 * A guide locating a payload that's fully comparable. Assumes that the tree's
 * structural order is the same as the natural order of the payloads.
 */
private class IntrinsicOrderGuide<PayloadT : Comparable<PayloadT>>(
    /**
     * The payload that the guide is looking for.
     */
    private val locatedPayload: PayloadT,
) : Guide<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): GuideInstruction = GuideInstruction.comparing(
        expected = locatedPayload,
        actual = payload,
    )
}
