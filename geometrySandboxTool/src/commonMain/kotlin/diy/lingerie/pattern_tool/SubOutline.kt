package diy.lingerie.pattern_tool

/**
 * An extracted subset of the outline
 */
internal class SubOutline(
    /**
     * A list of sequential verges, where each verge starts at the end of the
     * previous one. The last verge does not connect to the first one.
     */
    val sequentialVerges: List<Outline.Verge>,
) {
    init {
        require(sequentialVerges.isNotEmpty())
    }

    fun close(
        closingEdgeMetadata: Outline.EdgeMetadata,
    ): Outline {
        val firstVerge = sequentialVerges.first()
        val lastVerge = sequentialVerges.last()

        val closingVerge = Outline.Verge.Companion.line(
            startAnchor = lastVerge.endAnchor,
            edgeMetadata = closingEdgeMetadata,
            endAnchor = firstVerge.startAnchor,
        )

        return Outline.connect(
            cyclicVerges = sequentialVerges + closingVerge,
        )
    }
}
