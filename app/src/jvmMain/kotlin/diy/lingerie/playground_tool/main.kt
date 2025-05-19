package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.geometry.curves.toCoordRange
import diy.lingerie.geometry.playground.Playground
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricCurveFunction
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricCurveFunction.Companion.primaryTRange
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.utils.iterable.LinSpace
import java.nio.file.Path

class MainCommand : CliktCommand() {
    val outputFilePath: Path by argument().path(
        mustExist = false,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the output file")

    override fun run() {
        val bezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(863.426829231712, 303.18800785949134),
            secondControl = Point(53.73076075494464, 164.97814335091425),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val subCurves = LinSpace.generateSubRanges(
            range = primaryTRange,
            sampleCount = 12,
        ).map { tRange ->
            bezierCurve.trim(coordRange = tRange.toCoordRange()!!)
        }.toList()

        val loweredCurves = subCurves.map {
            it.basisFunction.lower()
        }

        val playground = Playground(
            items = loweredCurves.map { loweredCurve ->
                Playground.QuadraticBezierBinomialItem(
                    color = SimpleColor.red,
                    quadraticBezierBinomial = loweredCurve,
                )
            },
        )

        playground.writeToFile(
            filePath = outputFilePath,
        )
    }
}

fun main(args: Array<String>) {
    MainCommand().main(argv = args)
}
