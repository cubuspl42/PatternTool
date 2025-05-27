package diy.lingerie.pattern_tool.pattern_document

import dev.toolkt.dom.pure.fo.FoRoot
import diy.lingerie.pattern_tool.PaperSizeConstants
import java.nio.file.Path

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

    fun dump(
        dumpDirectoryPath: Path,
    ) {
        patternPages.forEachIndexed { pageIndex, page ->
            page.dump(
                dumpDirectoryPath = dumpDirectoryPath,
                pageIndex = pageIndex,
            )
        }
    }
}
