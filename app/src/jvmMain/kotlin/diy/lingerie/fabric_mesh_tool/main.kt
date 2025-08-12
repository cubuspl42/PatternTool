package diy.lingerie.fabric_mesh_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.path
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.svg.PureSvgCircle
import dev.toolkt.dom.pure.svg.PureSvgGroup
import dev.toolkt.dom.pure.svg.PureSvgLine
import dev.toolkt.dom.pure.svg.PureSvgPath
import dev.toolkt.dom.pure.svg.PureSvgRoot
import dev.toolkt.dom.pure.svg.PureSvgShape
import dev.toolkt.dom.pure.svg.parse
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.splines.ClosedSpline
import dev.toolkt.geometry.splines.Spline
import diy.lingerie.geometry.svg_utils.importSvgPath
import java.nio.file.Path
import kotlin.io.path.reader

private const val threadGap = 10.0

object GenerateTemplateCommand : CliktCommand() {
    const val rowCount = 10

    const val columnCount = 10

    val stroke = PureSvgShape.Stroke(
        color = PureColor.black,
        width = 0.5,
    )

    val outputFilePath: Path by argument().path(
        mustExist = false,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the output SVG file")

    override fun run() {
        val width = columnCount * threadGap
        val height = rowCount * threadGap

        val gridLines = listOf(
            (0..columnCount).map { i ->
                val y = i * threadGap

                PureSvgLine(
                    start = Point(
                        x = 0.0,
                        y = y,
                    ),
                    end = Point(
                        x = width,
                        y = y,
                    ),
                    stroke = stroke,
                )
            } + (0..rowCount).map { j ->
                val x = j * threadGap

                PureSvgLine(
                    start = Point(
                        x = x,
                        y = 0.0,
                    ),
                    end = Point(
                        x = x,
                        y = height,
                    ),
                    stroke = stroke,
                )
            },
        ).flatten()

        val originCircle = PureSvgCircle(
            center = Point(
                x = width / 2.0,
                y = height / 2.0,
            ),
            radius = threadGap / 4.0,
            stroke = null,
            fill = PureSvgShape.Fill.Specified(
                color = PureColor.red,
            ),
        )

        val svgRoot = PureSvgRoot(
            width = width.px, height = height.px,
            graphicsElements = listOf(
                PureSvgGroup(
                    children = gridLines + originCircle,
                ),
            ),
        )

        svgRoot.writeToFile(
            filePath = outputFilePath,
        )

        println("SVG template generated at: $outputFilePath")
    }
}

object LoadCommand : CliktCommand() {
    val svgFilePath: Path by argument().path(
        mustExist = true,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the SVG file (with flattened transforms)")

    override fun run() {
        val svgRoot = PureSvgRoot.parse(
            reader = svgFilePath.reader(),
        )

        val shapes = svgRoot.flatten()

        fun isPointAlignedCorrectly(point: Point): Boolean = when {
            point.x % threadGap != 0.0 -> false
            point.y % threadGap != 0.0 -> false
            else -> true
        }

        fun isLineAlignedCorrectly(line: PureSvgLine): Boolean = when {
            !isPointAlignedCorrectly(line.start) -> false
            !isPointAlignedCorrectly(line.end) -> false
            else -> true
        }

        println(shapes)

        val originCircle = shapes.filterIsInstance<PureSvgCircle>().singleOrNull()
            ?: error("Expected a single circle in the SVG file, but found none or more than one.")

        if (!isPointAlignedCorrectly(originCircle.center)) {
            error("Origin circle is not aligned with the thread gap of $threadGap.")
        }

        val gridLines = shapes.filterIsInstance<PureSvgLine>()

        gridLines.forEach {
            if (!isLineAlignedCorrectly(it)) {
                error("Line is not aligned with the thread gap of $threadGap.")
            }
        }

        val path = shapes.filterIsInstance<PureSvgPath>().singleOrNull()
            ?: error("Expected a single path in the SVG file, but found none or more than one.")

        val closedSpline = Spline.importSvgPath(svgPath = path) as? ClosedSpline
            ?: error("Expected a closed spline, but found an open spline or a different type.")

        val translation = Point.origin.translationTo(
            target = originCircle.center,
        )

        val translatedSpline = closedSpline.transformBy(
            transformation = translation.invert(),
        )

        println("Translated spline:")
        println(translatedSpline)
    }
}

class MainCommand : CliktCommand() {
    override fun run() = Unit
}

fun main(args: Array<String>) = MainCommand().subcommands(GenerateTemplateCommand, LoadCommand).main(args)
