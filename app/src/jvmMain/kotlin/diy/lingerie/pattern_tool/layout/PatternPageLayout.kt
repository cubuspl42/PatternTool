package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PaperSizeConstants
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.pattern_tool.pattern_document.PatternPage
import diy.lingerie.simple_dom.svg.PureSvgGroup
import diy.lingerie.simple_dom.svg.PureSvgRoot
import kotlinx.serialization.Serializable

@Serializable
data class PatternPageLayout(
    val pieceLayoutById: Map<PatternPieceId, PatternPieceLayout>,
) {
    companion object {
        fun reconstruct(
            pageSvgRoot: PureSvgRoot,
        ): PatternPageLayout = PatternPageLayout(
            pieceLayoutById = pageSvgRoot.graphicsElements.mapNotNull { svgElement ->
                (svgElement as? PureSvgGroup)?.let { svgGroup ->
                    PatternPieceLayout.reconstruct(svgGroup)
                }
            }.toMap(),
        )
    }

    fun layOut(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): PatternPage = PatternPage(
        pageSvgRoot = PureSvgRoot(
            width = PaperSizeConstants.A4.width,
            height = PaperSizeConstants.A4.height,
            graphicsElements = pieceLayoutById.map { (id, patternPieceLayout) ->
                val patternPiece =
                    patternPieceById[id] ?: throw IllegalArgumentException("Pattern piece with id $id not found")

                patternPieceLayout.layOut(
                    pieceId = id,
                    patternPiece = patternPiece,
                )
            },
        )
    )
}
