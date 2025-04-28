package diy.lingerie.pattern_tool

data class PatternPieceOutlineSet(
    val patternPieceOutlineById: Map<PatternPieceId, Outline>,
) {
    fun getPatternPieceOutlineById(
        id: PatternPieceId,
    ): Outline {
        return patternPieceOutlineById[id]
            ?: throw IllegalArgumentException("Pattern piece outline with id $id not found")
    }
}

