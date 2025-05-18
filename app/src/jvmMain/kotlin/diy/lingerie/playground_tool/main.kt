package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
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
            firstControl = Point(863.426829231712, 303.18800785949134),
            secondControl = Point(53.73076075494464, 164.97814335091425),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(382.2960291124364, 335.5675928528492),
            firstControl = Point(370.41409366476535, 370.845949740462),
            secondControl = Point(402.03174182196125, 441.30516989916543),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val originalPoint = Point(569.3968082522356, 571.1910917432544)

        val foundPoint = Point(561.8031822858212, 567.9882255688325)

        val midPoints = listOf(
            Point(214.60491394516154, 506.75913783218203),
            Point(245.45044051875894, 497.1497999563284),
            Point(274.0591519685386, 487.63852021100473),
            Point(300.50098490026767, 478.23735710914383),
            Point(324.84587591971365, 468.9583691636781),
            Point(347.1637616326438, 459.8136148875405),
            Point(367.52457864482534, 450.8151527936635),
            Point(385.9982635620256, 441.97504139497994),
            Point(402.65475299001173, 433.30533920442224),
            Point(417.56398353455126, 424.81810473492317),
            Point(430.7958918014112, 416.5253964994154),
            Point(442.420414396359, 408.4392730108315),
            Point(452.50748792516197, 400.57179278210435),
            Point(461.12704899358727, 392.93501432616637),
            Point(468.3490342074023, 385.5409961559504),
            Point(474.24338017237426, 378.40179678438886),
            Point(478.8800234942707, 371.52947472441485),
            Point(482.3289007788584, 364.9360884889605),
            Point(484.65994863190514, 358.633696590959),
            Point(485.9431036591778, 352.63435754334245),
            Point(486.248302466444, 346.95012985904395),
            Point(485.6454816594709, 341.59307205099594),
            Point(484.2045778440258, 336.57524263213116),
            Point(481.99552762587587, 331.9087001153822),
            Point(479.0882676107885, 327.60550301368187),
        )

        val closerPoints = listOf(
            Point(566.6257028904326, 571.7339876473815),
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
                Playground.PointItem(
                    point = originalPoint,
                ),
                Playground.PointItem(
                    color = SimpleColor.green,
                    point = foundPoint,
                )
//                Playground.BezierCurveItem(
//                    color = SimpleColor.blue,
//                    bezierCurve = secondBezierCurve,
//                ),
            ) + closerPoints.map { intersectionPoint ->
                Playground.PointItem(
                    color = SimpleColor.blue,
                    point = intersectionPoint,
                )
            } + midPoints.map { intersectionPoint ->
                Playground.PointItem(
                    color = SimpleColor.lightGray,
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
