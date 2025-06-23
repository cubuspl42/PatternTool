package dev.toolkt.core.data_structures.binary_tree

import kotlin.random.Random

/**
 * A guide that can be used to find a specific location in the binary tree. A
 * guide (typically) takes some assumptions about the structure of the tree,
 * i.e. how the node's index in the order corresponds to its payload.
 */
interface Guide<in PayloadT> {
    /**
     * An instruction on how to proceed with the search in a binary tree.
     */
    sealed interface Instruction {
        companion object {
            fun <T : Comparable<T>> comparing(
                expected: T,
                actual: T,
            ): Instruction {
                val result = expected.compareTo(actual)

                return when {
                    result == 0 -> Guide.StopInstruction

                    else -> Guide.TurnInstruction(
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
     * An instruction to turn to (recurse to) a side of the tree.
     */
    data class TurnInstruction(
        /**
         * The side of the tree to turn to
         */
        val side: BinaryTree.Side,
    ) : Instruction

    /**
     * An instruction to stop, meaning that the payload has been found
     */
    data object StopInstruction : Instruction

    /**
     * Instructs on how to proceed with the given [payload].
     */
    fun instruct(
        payload: PayloadT,
    ): Instruction
}

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
    ): Guide.Instruction = Guide.Instruction.comparing(
        expected = locatedPayload,
        actual = payload,
    )
}

/**
 * A guide locating a payload that's partially comparable (has a property called
 * "key" that is comparable). Assumes that the tree's structural order corresponds
 * to the natural order of the payload keys.
 */
private class KeyOrderGuide<PayloadT, KeyT : Comparable<KeyT>>(
    /**
     * The key that the guide is looking for.
     */
    private val locatedKey: KeyT,
    /**
     * A function extracting a key from the payload.
     */
    private val selector: (PayloadT) -> KeyT,
) : Guide<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): Guide.Instruction = Guide.Instruction.comparing(
        expected = locatedKey,
        actual = selector(payload),
    )
}

/**
 * A guide that's turning randomly, but not stopping until a free location is
 * reached. Takes no actual assumptions about the structure of the tree.
 * The probability distribution is non-uniform, meaning that the chance on reaching
 * a given free location might be different for different locations.
 */
private class RandomGuide<PayloadT>(
    private val random: Random,
) : Guide<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): Guide.Instruction = Guide.TurnInstruction(
        side = random.nextSide(),
    )
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.findLocationGuided(
    guide: Guide<PayloadT>,
): BinaryTree.Location<PayloadT, MetadataT> = this.findLocationGuided(
    location = BinaryTree.RootLocation,
    guide = guide,
)

private tailrec fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.findLocationGuided(
    location: BinaryTree.Location<PayloadT, MetadataT>,
    guide: Guide<PayloadT>,
): BinaryTree.Location<PayloadT, MetadataT> {
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
        Guide.StopInstruction -> return location

        is Guide.TurnInstruction -> {
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

fun <PayloadT : Comparable<PayloadT>, MetadataT> BinaryTree<PayloadT, MetadataT>.find(
    payload: PayloadT,
): BinaryTree.Location<PayloadT, MetadataT> = findLocationGuided(
    guide = IntrinsicOrderGuide(
        locatedPayload = payload,
    ),
)

fun <PayloadT, KeyT : Comparable<KeyT>, MetadataT> BinaryTree<PayloadT, MetadataT>.findBy(
    key: KeyT,
    selector: (PayloadT) -> KeyT,
): BinaryTree.Location<PayloadT, MetadataT> = findLocationGuided(
    guide = KeyOrderGuide(
        locatedKey = key,
        selector = selector,
    ),
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getRandomFreeLocation(
    random: Random,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    guide = RandomGuide(
        random = random,
    ),
)

fun Random.nextSide(): BinaryTree.Side = when (nextBoolean()) {
    true -> BinaryTree.Side.Left
    false -> BinaryTree.Side.Right
}
