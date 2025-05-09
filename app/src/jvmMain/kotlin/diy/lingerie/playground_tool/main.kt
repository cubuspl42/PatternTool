package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.BoundingBox
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
        val bezierCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(1068.5394763946533, 253.16610717773438),
            secondControl = Point(-125.00849723815918, 252.71710205078125),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val boundingBox = BoundingBox(
            topLeft = Point(273.80049324035645, 312.1176405539444),
            width = 397.6180114746094,
            height = 178.08746808863373,
        )

        val playground = Playground(
            items = listOf(
                Playground.BezierCurveItem(
                    bezierCurve = bezierCurve,
                ),
                Playground.BoundingBoxItem(
                    boundingBox = boundingBox,
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
