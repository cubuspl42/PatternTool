package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PaperSizeConstants
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.pattern_tool.pattern_document.PatternPage
import diy.lingerie.simple_dom.svg.SvgRoot
import kotlinx.serialization.Serializable

@Serializable
data class PatternPageLayout(
    val pieceLayoutById: Map<PatternPieceId, PatternPieceLayout>,
) {
    fun layOut(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): PatternPage = PatternPage(
        pageSvgRoot = SvgRoot(
            width = PaperSizeConstants.A4.width,
            height = PaperSizeConstants.A4.height,
            children = pieceLayoutById.map { (id, patternPieceLayout) ->
                val patternPiece =
                    patternPieceById[id] ?: throw IllegalArgumentException("Pattern piece with id $id not found")

                patternPieceLayout.layOut(patternPiece = patternPiece)
            },
        )
    )
}
