package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.pattern_document.PatternDocument
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId

data class PatternLayout(
    val patternPageLayouts: List<PatternPageLayout>,
) {
    fun layOut(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): PatternDocument {
        return PatternDocument(
            patternPages = patternPageLayouts.map { patternPieceLayout ->
                patternPieceLayout.layOut(patternPieceById = patternPieceById)
            },
        )
    }
}
