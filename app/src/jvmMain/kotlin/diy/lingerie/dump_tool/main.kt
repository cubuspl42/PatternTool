package diy.lingerie.dump_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.splines.OpenSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.svg.importSvgPath
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.simple_dom.svg.SvgLine
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRectangle
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.SvgShape
import java.nio.file.Path
import kotlin.io.path.reader

enum class DumpSplineStrategyId(val strategy: DumpPathStrategy) {
    BasisFunctions(DumpBasisFunctionsStrategy),
    Curves(DumpPrimitiveCurvesStrategy),
    Spline(DumpSplineStrategy),
}

sealed class DumpPathStrategy {
    abstract fun dumpSpline(
        spline: Spline,
    )
}

data object DumpSplineStrategy : DumpPathStrategy() {
    override fun dumpSpline(spline: Spline) {
        println(spline.toReprString())
    }
}

sealed class DumpSegmentCurvesStrategy : DumpPathStrategy() {
    final override fun dumpSpline(spline: Spline) {
        spline.segmentCurves.forEachIndexed { index, segmentCurve ->
            println("Curve #${index}:")
            println()
            dumpSegmentCurve(segmentCurve)
            println()
        }
    }

    abstract fun dumpSegmentCurve(
        curve: PrimitiveCurve,
    )
}

data object DumpPrimitiveCurvesStrategy : DumpSegmentCurvesStrategy() {
    override fun dumpSegmentCurve(curve: PrimitiveCurve) {
        println(curve.toReprString())
    }
}

data object DumpBasisFunctionsStrategy : DumpSegmentCurvesStrategy() {
    override fun dumpSegmentCurve(curve: PrimitiveCurve) {
        println(curve.basisFunction.toReprString())
    }
}

class Tool : CliktCommand() {
    val dumpSplineStrategyId: DumpSplineStrategyId by option("--dump-spline").enum<DumpSplineStrategyId>().required()

    val svgFilePath: Path by argument().path(
        mustExist = true,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the SVG file")

    override fun run() {
        val svgRoot = SvgRoot.parse(
            reader = svgFilePath.reader(),
        )

        val svgShapes = svgRoot.flatten(
            baseTransformation = Transformation.Identity,
        )

        val (svgRects, otherSvgShapes) = svgShapes.partition {
            it is SvgRectangle
        }

        val singleSvgRect = svgRects.singleOrNull()
            ?: error("Expected a single rectangle, but found ${svgRects.size} rectangles")

        val areaRectangle = singleSvgRect as SvgRectangle

        otherSvgShapes.forEachIndexed { index, svgShape ->
            println("SVG Shape #$index")

            processShape(
                areaRectangle = areaRectangle,
                svgShape = svgShape,
            )
        }
    }

    private fun processShape(
        areaRectangle: SvgRectangle,
        svgShape: SvgShape,
    ) {
        when (svgShape) {
            is SvgLine -> {
                processLine(
                    areaRectangle = areaRectangle,
                    svgLine = svgShape,
                )
            }

            is SvgPath -> {
                processPath(
                    areaRectangle = areaRectangle,
                    svgPath = svgShape,
                )
            }

            else -> throw UnsupportedOperationException("Unsupported SVG shape: $svgShape")
        }
    }

    private fun processLine(
        areaRectangle: SvgRectangle,
        svgLine: SvgLine,
    ) {
        println("(SVG line)")

        when {
            areaRectangle.contains(svgLine.start) && areaRectangle.contains(svgLine.end) -> {
                dumpLineSegment(
                    start = svgLine.start,
                    end = svgLine.end,
                )
            }

            areaRectangle.contains(svgLine.start) -> {
                dumpRay(
                    startingPoint = svgLine.start,
                    finalPoint = svgLine.end,
                )
            }

            areaRectangle.contains(svgLine.end) -> {
                dumpRay(
                    startingPoint = svgLine.end,
                    finalPoint = svgLine.start,
                )
            }

            else -> {
                dumpLine(
                    pointA = svgLine.start,
                    pointB = svgLine.end,
                )
            }
        }
    }

    private fun dumpLineSegment(
        start: Point,
        end: Point,
    ) {
        val lineSegment = LineSegment(
            start = start,
            end = end,
        )

        println("Line segment:")
        println(lineSegment.toReprString())
    }

    private fun dumpRay(
        startingPoint: Point,
        finalPoint: Point,
    ) {
        val ray = startingPoint.castRayTo(
            target = finalPoint,
        ) ?: throw UnsupportedOperationException("Ray cannot be created")

        println("Ray:")
        println(ray.toReprString())
    }

    private fun dumpLine(
        pointA: Point,
        pointB: Point,
    ) {
        TODO()
    }

    private fun processPath(
        areaRectangle: SvgRectangle,
        svgPath: SvgPath,
    ) {
        val hexColorString = svgPath.stroke?.let {  stroke ->
            "[${svgPath.stroke.color.toHexString()}]"
        }

        println("(SVG path, color: $hexColorString)")

        val spline = Spline.importSvgPath(svgPath = svgPath)

        val lineSegment = (spline as? OpenSpline)?.toLineSegment()

        when {
            lineSegment != null -> {
                processLine(
                    areaRectangle = areaRectangle,
                    svgLine = SvgLine(
                        start = lineSegment.start,
                        end = lineSegment.end,
                    ),
                )
            }

            else -> {
                dumpSpline(spline = spline)
            }
        }
    }

    private fun dumpSpline(
        spline: Spline,
    ) {
        println("Spline:")
        dumpSplineStrategyId.strategy.dumpSpline(spline)
    }
}

fun main(args: Array<String>) {
    Tool().main(argv = args)
}
