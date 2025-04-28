package diy.lingerie.pattern_tool.pattern_document

import diy.lingerie.simple_dom.fo.FoSvgBlock
import diy.lingerie.simple_dom.svg.SvgRoot
import java.nio.file.Path

data class PatternPage(
    val pageSvgRoot: SvgRoot,
) {
    fun format(): FoSvgBlock = FoSvgBlock(
        svgElement = pageSvgRoot,
    )

    fun dump(
        dumpDirectoryPath: Path,
        pageIndex: Int,
    ) {
        val fileName = "%02d.svg".format(pageIndex)

        pageSvgRoot.writeToFile(
            filePath = dumpDirectoryPath.resolve(fileName),
        )
    }
}
