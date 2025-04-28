package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PaperSizeConstants
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.simple_dom.fo.FoSvgBlock
import diy.lingerie.simple_dom.svg.SvgRoot

data class PatternPageLayout(
    val patternPieceLayoutById: Map<PatternPieceId, PatternPieceLayout>,
) {
    fun toFoBlock(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): FoSvgBlock = FoSvgBlock(
        svgElement = SvgRoot(
            width = PaperSizeConstants.A4.width,
            height = PaperSizeConstants.A4.height,
            children = patternPieceLayoutById.map { (id, patternPieceLayout) ->
                val patternPiece =
                    patternPieceById[id] ?: throw IllegalArgumentException("Pattern piece with id $id not found")

                patternPieceLayout.toSvgElement(patternPiece = patternPiece)
            },
        ),
    )
}
