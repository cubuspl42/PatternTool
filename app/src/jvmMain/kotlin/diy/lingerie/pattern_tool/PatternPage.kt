package diy.lingerie.pattern_tool

import diy.lingerie.geometry.toSvgPath
import diy.lingerie.simple_dom.fo.FoSvgBlock
import diy.lingerie.simple_dom.svg.SvgRoot

data class PatternPage(
    val patternPieces: List<PatternPiece>,
) {
    fun toPageFoBlock(): FoSvgBlock = FoSvgBlock(
        svgElement = SvgRoot(
            width = PaperSizeConstants.A4.width,
            height = PaperSizeConstants.A4.height,
            children = patternPieces.map { patternPiece ->
                patternPiece.outlineInnerSplineGlobal.toSvgPath()
            }
        ),
    )
}
