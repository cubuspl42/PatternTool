package diy.lingerie.pattern_tool

import diy.lingerie.pattern_tool.layout.PatternLayout
import diy.lingerie.simple_dom.svg.SvgRoot
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.reader

class PatternGenerator(
    private val workingDirectoryPath: Path,
    private val patternPiecePreparator: PatternPiecePreparator,
    private val patternLayout: PatternLayout,
) {
    private val inputDirectoryPath: Path
        get() = workingDirectoryPath.resolve("input")

    private val outputDirectoryPath: Path
        get() = workingDirectoryPath.resolve("output")

    private val pagesOutputDirectoryPath: Path
        get() = outputDirectoryPath.resolve("pages")

    fun generatePattern() {
        outputDirectoryPath.createDirectories()
        pagesOutputDirectoryPath.createDirectories()

        val svgRootByName = inputDirectoryPath.listDirectoryEntries("*.svg").associate { filePath ->
            filePath.nameWithoutExtension to SvgRoot.parse(
                reader = filePath.reader(),
            )
        }

        val patternPieceOutlineById = patternPiecePreparator.preparePatternPieceOutlines(
            svgRootByName = svgRootByName,
        )

        val patternPieceById = patternPieceOutlineById.mapValues { (_, outline) ->
            PatternPiece(
                outline = outline,
            )
        }

        val patternFoRoot = patternLayout.toFoRoot(
            patternPieceById = patternPieceById,
        )

        patternFoRoot.writePdfToFile(
            pdfFilePath = outputDirectoryPath.resolve("pattern.pdf"),
        )
    }
}
