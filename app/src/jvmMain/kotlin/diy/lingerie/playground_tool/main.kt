package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.playground.Playground
import java.nio.file.Path

class MainCommand : CliktCommand() {
    val outputFilePath: Path by argument().path(
        mustExist = false,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the output file")

    override fun run() {
        val firstCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(1068.5394763946533, 253.16610717773438),
            secondControl = Point(-125.00849723815918, 252.71710205078125),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val secondCurve = BezierCurve(
            start = Point(372.6355152130127, 191.58710479736328),
            firstControl = Point(496.35252571105957, 852.5531311035156),
            secondControl = Point(442.4235095977783, -54.72489929199219),
            end = Point(569.3854846954346, 487.569091796875),
        )

        val points = listOf(
            Point(400.0364120882783, 325.7513850441302),
            Point(415.9864000101944, 388.18876477651054),
            Point(433.78055261270123, 434.84732656764527),
            Point(459.06587349145525, 424.28587808679634),
            Point(462.2096738267076, 414.2195778544469),
            Point(491.64500747999983, 312.8831093313188),
            Point(515.05453270079, 316.0676656534099),
            Point(540.6332704779081, 378.60112801527333),
            Point(561.4569242282377, 454.624570407085),
        )

        val playground = Playground(
            items = listOf(
                Playground.BezierCurveItem(
                    bezierCurve = firstCurve,
                ),
                Playground.BezierCurveItem(
                    bezierCurve = secondCurve,
                ),
            ) + points.map { point ->
                Playground.PointItem(
                    point = point,
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
