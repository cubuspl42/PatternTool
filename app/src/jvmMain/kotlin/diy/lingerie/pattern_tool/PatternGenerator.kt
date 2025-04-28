@file:OptIn(ExperimentalSerializationApi::class)

package diy.lingerie.pattern_tool

import diy.lingerie.pattern_tool.layout.PatternLayout
import diy.lingerie.simple_dom.svg.SvgRoot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.outputStream
import kotlin.io.path.reader

class PatternGenerator(
    private val workingDirectoryPath: Path,
    private val patternPiecePreparator: PatternPiecePreparator,
    private val patternLayout: PatternLayout,
) {
    companion object {
        private val json = Json { prettyPrint = true }
    }

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
            filePath.nameWithoutExtension to SvgRoot.parse(
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

        val patternDocument = patternLayout.layOut(
            patternPieceById = patternPieceById,
        )

        patternDocument.dump(
            dumpDirectoryPath = pagesDumpDirectoryPath,
        )

        workingDirectoryPath.resolve("layout.json").outputStream().use {
            json.encodeToStream(
                value = patternLayout,
                stream = it,
            )
        }

        patternDocument.format().writePdfToFile(
            pdfFilePath = outputDirectoryPath.resolve("pattern.pdf"),
        )
    }
}
