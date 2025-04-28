package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.pattern_tool.pattern_document.PatternDocument
import kotlinx.serialization.Serializable

@Serializable
data class PatternLayout(
    val pageLayouts: List<PatternPageLayout>,
) {
    fun layOut(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): PatternDocument = PatternDocument(
        patternPages = pageLayouts.map { patternPieceLayout ->
            patternPieceLayout.layOut(patternPieceById = patternPieceById)
        },
    )
}
