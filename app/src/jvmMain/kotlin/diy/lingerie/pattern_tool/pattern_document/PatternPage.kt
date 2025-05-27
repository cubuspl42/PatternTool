package diy.lingerie.pattern_tool.pattern_document

import dev.toolkt.dom.pure.svg.PureSvgRoot
import dev.toolkt.dom.pure.fo.FoSvgBlock
import java.nio.file.Path

data class PatternPage(
    val pageSvgRoot: PureSvgRoot,
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
