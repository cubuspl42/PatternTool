@file:OptIn(ExperimentalSerializationApi::class)

package diy.lingerie.pattern_tool

import dev.toolkt.dom.pure.svg.PureSvgRoot
import dev.toolkt.dom.pure.svg.parse
import diy.lingerie.pattern_tool.layout.PatternLayout
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.reader

class PatternGenerator(
    private val workingDirectoryPath: Path,
    private val patternPiecePreparator: PatternPiecePreparator,
) {
    private val inputDirectoryPath: Path
        get() = workingDirectoryPath.resolve("input")

    private val outputDirectoryPath: Path
        get() = workingDirectoryPath.resolve("output")

    private val pagesDumpDirectoryPath: Path
        get() = outputDirectoryPath.resolve("pages")

    fun generatePattern(
        direction: PatternGenerationDirection,
    ) {
        outputDirectoryPath.createDirectories()
        pagesDumpDirectoryPath.createDirectories()

        val svgRootByName = inputDirectoryPath.listDirectoryEntries("*.svg").associate { filePath ->
            filePath.nameWithoutExtension to PureSvgRoot.parse(
                reader = filePath.reader(),
            )
        }

        val patternPieceOutlineSet = patternPiecePreparator.preparePatternPieceOutlines(
            svgRootByName = svgRootByName,
        )

        val patternPieceById = patternPieceOutlineSet.patternPieceOutlineById.mapValues { (_, outline) ->
            PatternPiece(
                outline = outline,
            )
        }

        val patternLayout = when (direction) {
            PatternGenerationDirection.Forward -> PatternLayout.load(
                workingDirectoryPath = workingDirectoryPath,
            )

            PatternGenerationDirection.Backward -> PatternLayout.reconstruct(
                dumpDirectoryPath = pagesDumpDirectoryPath,
            )
        }

        val patternDocument = patternLayout.layOut(
            patternPieceById = patternPieceById,
        )

        when (direction) {
            PatternGenerationDirection.Forward -> {
                patternDocument.dump(
                    dumpDirectoryPath = pagesDumpDirectoryPath,
                )
            }

            PatternGenerationDirection.Backward -> {
                patternLayout.dump(
                    workingDirectoryPath = workingDirectoryPath,
                )
            }
        }

        patternDocument.format().writePdfToFile(
            pdfFilePath = outputDirectoryPath.resolve("pattern.pdf"),
        )
    }
}
