package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.geometry.playground.Playground
import diy.lingerie.simple_dom.SimpleColor
import java.nio.file.Path

class MainCommand : CliktCommand() {
    val outputFilePath: Path by argument().path(
        mustExist = false,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the output file")

    override fun run() {
        val lineSegment = LineSegment(
            start = Point(401.14355433959827, 374.2024184921395),
            end = Point(601.1435543395982, 374.2024184921395),
        )

        val firstBezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(422.77519184542564, 441.5255275486571),
            secondControl = Point(482.0980368984025, 387.5853838361354),
            end = Point(486.0476425340348, 351.778389940191),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(382.2960291124364, 335.5675928528492),
            firstControl = Point(370.41409366476535, 370.845949740462),
            secondControl = Point(402.03174182196125, 441.30516989916543),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val intersectionPoints = listOf(
            Point(355.98023324908263, 456.00230813468403),
        )

        val playground = Playground(
            items = listOf(
//                Playground.LineSegmentItem(
//                    color = SimpleColor.green,
//                    lineSegment = lineSegment,
//                ),
                Playground.BezierCurveItem(
                    color = SimpleColor.red,
                    bezierCurve = firstBezierCurve,
                ),
                Playground.BezierCurveItem(
                    color = SimpleColor.blue,
                    bezierCurve = secondBezierCurve,
                ),
            ) + intersectionPoints.map { intersectionPoint ->
                Playground.PointItem(
                    point = intersectionPoint,
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
