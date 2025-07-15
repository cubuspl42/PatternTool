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
        val start = Point(277.26681060791014, 236.51385116577148)
        val end = Point(663.6991928100585, 231.08415603637695)

        // A simple "smile" curve, but degenerating to a quadratic curve
        val bezierCurve = BezierCurve(
            start = start,
            firstControl = Point(414.2205947875977, 355.1834526062012),
            secondControl = Point(543.0313888549805, 353.3735542297363),
            end = end,
        )

        val playground = Playground(
            items = listOf(
                Playground.BezierCurveItem(
                    color = PureColor.blue,
                    bezierCurve = bezierCurve,
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
