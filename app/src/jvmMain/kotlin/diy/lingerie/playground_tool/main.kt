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
            start = Point(383.0995044708252, 275.80810546875),
            firstControl = Point(435.23948860168457, 325.49310302734375),
            secondControl = Point(510.3655261993408, 384.4371032714844),
            end = Point(614.6575183868408, 453.4740905761719),
        )

        val secondCurve = BezierCurve(
            start = Point(372.14351081848145, 439.6011047363281),
            firstControl = Point(496.5914783477783, 370.8171081542969),
            secondControl = Point(559.4554920196533, 307.91810607910156),
            end = Point(582.3854846954346, 253.8291015625),
        )

        val intersectionPoint = Point(488.177482, 364.171107)

        val playground = Playground(
            items = listOf(
                Playground.BezierCurveItem(
                    bezierCurve = firstCurve,
                ),
                Playground.BezierCurveItem(
                    bezierCurve = secondCurve,
                ),
                Playground.PointItem(
                    point = intersectionPoint,
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
