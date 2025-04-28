package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PaperSizeConstants
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.simple_dom.fo.FoRoot

data class PatternLayout(
    val patternPageLayouts: List<PatternPageLayout>,
) {
    fun toFoRoot(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): FoRoot = FoRoot(
        pageWidth = PaperSizeConstants.A4.width,
        pageHeight = PaperSizeConstants.A4.height,
        blocks = patternPageLayouts.map { pageLayout ->
            pageLayout.toFoBlock(
                patternPieceById = patternPieceById,
            )
        },
    )
}
