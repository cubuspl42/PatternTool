package diy.lingerie.pattern_tool.pattern_document

import diy.lingerie.pattern_tool.PaperSizeConstants
import diy.lingerie.simple_dom.fo.FoRoot

data class PatternDocument(
    val patternPages: List<PatternPage>,
) {
    fun format(): FoRoot = FoRoot(
        pageWidth = PaperSizeConstants.A4.width,
        pageHeight = PaperSizeConstants.A4.height,
        blocks = patternPages.map { pageLayout ->
            pageLayout.format()
        },
    )
}
