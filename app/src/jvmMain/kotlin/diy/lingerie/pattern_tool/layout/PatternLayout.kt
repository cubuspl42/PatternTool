@file:OptIn(ExperimentalSerializationApi::class)

package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.pattern_tool.pattern_document.PatternDocument
import diy.lingerie.simple_dom.svg.PureSvgRoot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.outputStream
import kotlin.io.path.reader

@Serializable
data class PatternLayout(
    val pageLayouts: List<PatternPageLayout>,
) {
    companion object {
        private val json = Json { prettyPrint = true }

        private const val DEFAULT_FILENAME = "layout.json"

        fun load(
            workingDirectoryPath: Path,
        ): PatternLayout = workingDirectoryPath.resolve(DEFAULT_FILENAME).inputStream().use { fileInputStream ->
            json.decodeFromStream<PatternLayout>(stream = fileInputStream)
        }

        fun reconstruct(
            dumpDirectoryPath: Path,
        ): PatternLayout = PatternLayout(
            pageLayouts = dumpDirectoryPath.listDirectoryEntries("*.svg").mapIndexed { index, filePath ->
                val fileName = filePath.nameWithoutExtension

                val fileIndex = fileName.toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid file name: $fileName. Expected a number.")

                if (fileIndex != index) {
                    throw IllegalArgumentException("Invalid file name: $fileName. Expected a number in order.")
                }

                PatternPageLayout.reconstruct(
                    pageSvgRoot = PureSvgRoot.parse(
                        reader = filePath.reader(),
                    ),
                )
            },
        )
    }

    fun layOut(
        patternPieceById: Map<PatternPieceId, PatternPiece>,
    ): PatternDocument = PatternDocument(
        patternPages = pageLayouts.map { patternPieceLayout ->
            patternPieceLayout.layOut(patternPieceById = patternPieceById)
        },
    )

    fun dump(
        workingDirectoryPath: Path,
    ) {
        workingDirectoryPath.resolve(DEFAULT_FILENAME).outputStream().use {
            json.encodeToStream(
                value = this,
                stream = it,
            )
        }
    }
}
