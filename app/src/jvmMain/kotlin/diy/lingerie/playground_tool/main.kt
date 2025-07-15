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
        val bezierCurve = BezierCurve(
            start = Point(0.0, 200.0),
            firstControl = Point(100.0, 0.0),
            secondControl = Point(200.0, 200.0),
            end = Point(300.0, 0.0),
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
