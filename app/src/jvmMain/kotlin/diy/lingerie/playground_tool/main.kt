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

        // A loop split at its top
        val bezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val intersectionPoints = listOf(

            Point(501.14355433959827, 374.2024184921395),
        )

        val playground = Playground(
            items = listOf(
                Playground.LineSegmentItem(
                    color = SimpleColor.green,
                    lineSegment = lineSegment,
                ),
                Playground.BezierCurveItem(
                    color = SimpleColor.red,
                    bezierCurve = bezierCurve,
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
