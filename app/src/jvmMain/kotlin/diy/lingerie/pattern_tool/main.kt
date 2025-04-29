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
import diy.lingerie.simple_dom.svg.SvgRoot
import java.nio.file.Path

enum class PatternPieceId {
    UpperCup, InnerLowerCup, OuterLowerCup,
}

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
                    svgRootByName: Map<String, SvgRoot>,
                ): PatternPieceOutlineSet {
                    val upperCupSvgRoot =
                        svgRootByName["upperCup"] ?: throw IllegalArgumentException("upperCup not found")

                    val innerLowerCupSvgRoot =
                        svgRootByName["innerLowerCup"] ?: throw IllegalArgumentException("innerLowerCup not found")

                    val outerLowerCupSvgRoot =
                        svgRootByName["outerLowerCup"] ?: throw IllegalArgumentException("outerLowerCup not found")

                    return PatternPieceOutlineSet(
                        patternPieceOutlineById = mapOf(
                            PatternPieceId.UpperCup to Outline.loadSvg(
                                svgRoot = upperCupSvgRoot,
                            ),
                            PatternPieceId.InnerLowerCup to Outline.loadSvg(
                                svgRoot = innerLowerCupSvgRoot,
                            ),
                            PatternPieceId.OuterLowerCup to Outline.loadSvg(
                                svgRoot = outerLowerCupSvgRoot,
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
