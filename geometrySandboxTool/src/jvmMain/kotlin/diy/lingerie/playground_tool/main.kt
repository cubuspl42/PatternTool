package diy.lingerie.playground_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import diy.lingerie.tool_utils.Playground
import java.nio.file.Path

class MainCommand : CliktCommand() {
    val outputFilePath: Path by argument().path(
        mustExist = false,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the output file")

    override fun run() {
        val firstBezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(422.77519184542564, 441.5255275486571),
            secondControl = Point(482.0980368984025, 387.5853838361354),
            end = Point(484.0, 353.0),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(382.2960291124364, 335.5675928528492),
            firstControl = Point(370.41409366476535, 370.845949740462),
            secondControl = Point(402.03174182196125, 441.30516989916543),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val playground = Playground(
            items = listOf(
                Playground.BezierCurveItem(
                    color = PureColor.black,
                    bezierCurve = firstBezierCurve,
                ),
                Playground.BezierCurveItem(
                    color = PureColor.darkBlue,
                    bezierCurve = secondBezierCurve,
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
