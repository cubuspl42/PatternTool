package diy.lingerie.stat_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.svg.PureSvgRoot
import dev.toolkt.dom.pure.svg.parse
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.splines.OpenSpline
import diy.lingerie.tool_utils.Playground
import diy.lingerie.tool_utils.RecognizedShape
import java.nio.file.Path
import kotlin.io.path.reader

class Tool : CliktCommand() {
    val svgFilePath: Path by argument().path(
        mustExist = true,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the SVG file")

    val outputFilePath: Path by argument().path(
        mustExist = false,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the output file")

    override fun run() {
        val svgRoot = PureSvgRoot.parse(
            reader = svgFilePath.reader(),
        )

        val recognizedShapes = RecognizedShape.interpretSvg(
            svgRoot = svgRoot,
        )

        val openSplines = recognizedShapes.mapNotNull {
            (it as? RecognizedShape.RecognizedSpline)?.spline as? OpenSpline
        }

        val bezierCurves = openSplines.mapNotNull { it.toBezierCurve() }

        val playground = Playground(
            items = bezierCurves.flatMap { bezierCurve ->
                processBezierCurve(bezierCurve = bezierCurve)
            },
        )

        playground.writeToFile(
            filePath = outputFilePath,
        )
    }

    private fun processBezierCurve(
        bezierCurve: BezierCurve,
    ): List<Playground.Item> {
        val cubicItem = Playground.BezierCurveItem(
            color = PureColor.blue,
            bezierCurve = bezierCurve,
        )

        val quadraticItems = bezierCurve.lowerInRange(
            coordRange = OpenCurve.Coord.fullRange,
        ).map { segment ->
            Playground.QuadraticBezierBinomialItem(
                quadraticBezierBinomial = segment.approximationCurve,
            )
        }.toList()

        return listOf(cubicItem) + quadraticItems
    }
}

fun main(args: Array<String>) {
    Tool().main(argv = args)
}
