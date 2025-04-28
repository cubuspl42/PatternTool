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
import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.utils.getResourceAsReader
import java.nio.file.Path

class MainCommand : CliktCommand() {
    val direction: PatternGenerationDirection by option("--direction").enum<PatternGenerationDirection>().required()

    val flagStrict by option("--strict").flag()

    val outputPath: Path by argument().path(
        mustExist = true,
        canBeDir = true,
        canBeFile = false,
        mustBeWritable = true,
    ).help("Path to the output directory")

    override fun run() {
        val upperCupPatternPiece = PatternPiece(
            position = Point(x = 100.0, y = 20.0),
            rotationAngle = Angle.ofDegrees(90.0),
            outline = Outline.loadSvg(
                svgRoot = SvgRoot.parse(
                    reader = MainCommand::class.java.getResourceAsReader("patternPieceUpperCup.svg")!!,
                ),
            ),
        )

        val innerLowerCupPatternPiece = PatternPiece(
            position = Point(x = 0.0, y = 20.0),
            rotationAngle = Angle.ofDegrees(0.0),
            outline = Outline.loadSvg(
                svgRoot = SvgRoot.parse(
                    reader = MainCommand::class.java.getResourceAsReader("patternPieceInnerLowerCup.svg")!!,
                ),
            ),
        )

        val patternDocument = PatternDocument(
            pages = listOf(
                PatternPage(
                    patternPieces = listOf(
                        upperCupPatternPiece,
                        innerLowerCupPatternPiece,
                    ),
                ),
                PatternPage(
                    patternPieces = listOf(
                        upperCupPatternPiece,
                        innerLowerCupPatternPiece,
                    ),
                ),
            )
        )

        patternDocument.dumpPdf(
            outputPath = outputPath,
        )
    }
}

fun main(args: Array<String>) {
    MainCommand().main(argv = args)
}
