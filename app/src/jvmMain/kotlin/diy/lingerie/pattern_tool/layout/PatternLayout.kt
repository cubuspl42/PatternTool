package diy.lingerie.pattern_tool.layout

import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.pattern_tool.pattern_document.PatternDocument
import diy.lingerie.simple_dom.svg.SvgRoot
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.reader

@Serializable
data class PatternLayout(
    val pageLayouts: List<PatternPageLayout>,
) {
    companion object {
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
                    pageSvgRoot = SvgRoot.parse(
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
}
