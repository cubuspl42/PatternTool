package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
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
        val firstCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(684.4749774932861, 329.1851005554199),
            secondControl = Point(591.8677291870117, 214.5483512878418),
            end = Point(492.59773540496826, 197.3452272415161),
        )

        val secondCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val intersectionPoint1 = Point(492.59773540496826, 197.3452272415161)
        val intersectionPoint2 = Point(501.579334, 374.596689)

        val playground = Playground(
            items = listOf(
                Playground.BezierCurveItem(
                    color = SimpleColor.red,
                    bezierCurve = firstCurve,
                ),
                Playground.BezierCurveItem(
                    color = SimpleColor.blue,
                    bezierCurve = secondCurve,
                ),
                Playground.PointItem(
                    point = intersectionPoint1,
                ),
                Playground.PointItem(
                    point = intersectionPoint2,
                ),
            ),
        )

        playground.writeToFile(
            filePath = outputFilePath,
        )
    }
}

fun main(args: Array<String>) {
    MainCommand().main(argv = args)
}
