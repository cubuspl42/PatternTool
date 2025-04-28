package diy.lingerie.pattern_tool.pattern_document

import diy.lingerie.simple_dom.fo.FoSvgBlock
import diy.lingerie.simple_dom.svg.SvgRoot

data class PatternPage(
    val pageSvgRoot: SvgRoot,
) {
    fun format(): FoSvgBlock = FoSvgBlock(
        svgElement = pageSvgRoot,
    )
}
