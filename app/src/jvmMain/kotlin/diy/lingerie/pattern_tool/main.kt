package diy.lingerie.pattern_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.simple_dom.svg.PureSvgRoot
import java.nio.file.Path

enum class PatternPieceId {
    UpperCup, InnerLowerCup, OuterLowerCup,
}

val defaultEdgeMetadata = Outline.EdgeMetadata(
    seamAllowance = SeamAllowance(
        allowanceMm = 6.0,
    ),
)

class MainCommand : CliktCommand() {
    val direction: PatternGenerationDirection by option("--direction").enum<PatternGenerationDirection>().required()

    val flagStrict by option("--strict").flag()

    val workingDirectoryPath: Path by argument().path(
        mustExist = true,
        canBeDir = true,
        canBeFile = false,
        mustBeWritable = true,
    ).help("Path to the working directory")

    override fun run() {
        PatternGenerator(
            workingDirectoryPath = workingDirectoryPath,
            patternPiecePreparator = object : PatternPiecePreparator() {
                override fun preparePatternPieceOutlines(
                    svgRootByName: Map<String, PureSvgRoot>,
                ): PatternPieceOutlineSet {
                    val upperCupSvgRoot =
                        svgRootByName["upperCup"] ?: throw IllegalArgumentException("upperCup not found")

                    val upperCupEdgeMetadataMap = Outline.EdgeMetadataMap(
                        edgeMetadataByEdgeIndex = mapOf(),
                        defaultEdgeMetadata = defaultEdgeMetadata,
                    )

                    val innerLowerCupSvgRoot =
                        svgRootByName["innerLowerCup"] ?: throw IllegalArgumentException("innerLowerCup not found")

                    val innerLowerCupEdgeMetadataMap = Outline.EdgeMetadataMap(
                        edgeMetadataByEdgeIndex = mapOf(),
                        defaultEdgeMetadata = defaultEdgeMetadata,
                    )

                    val outerLowerCupSvgRoot =
                        svgRootByName["outerLowerCup"] ?: throw IllegalArgumentException("outerLowerCup not found")

                    val outerLowerCupEdgeMetadataMap = Outline.EdgeMetadataMap(
                        edgeMetadataByEdgeIndex = mapOf(),
                        defaultEdgeMetadata = defaultEdgeMetadata,
                    )

                    return PatternPieceOutlineSet(
                        patternPieceOutlineById = mapOf(
                            PatternPieceId.UpperCup to Outline.loadSvg(
                                svgRoot = upperCupSvgRoot,
                                edgeMetadataMap = upperCupEdgeMetadataMap,
                            ),
                            PatternPieceId.InnerLowerCup to Outline.loadSvg(
                                svgRoot = innerLowerCupSvgRoot,
                                edgeMetadataMap = innerLowerCupEdgeMetadataMap,
                            ),
                            PatternPieceId.OuterLowerCup to Outline.loadSvg(
                                svgRoot = outerLowerCupSvgRoot,
                                edgeMetadataMap = outerLowerCupEdgeMetadataMap,
                            ),
                        ),
                    )
                }
            },
        ).generatePattern(
            direction = direction,
        )
    }
}

fun main(args: Array<String>) {
    MainCommand().main(argv = args)
}
