package diy.lingerie.dump_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Ray
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.splines.Spline
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.tool_utils.RecognizedShape
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

        val recognizedShapes = RecognizedShape.interpretSvg(
            svgRoot = svgRoot,
        )

        recognizedShapes.forEachIndexed { index, recognizedShape ->
            println("Recognized shape #$index")

            when (recognizedShape) {
                is RecognizedShape.RecognizedLine -> TODO()

                is RecognizedShape.RecognizedLineSegment -> dumpLineSegment(
                    lineSegment = recognizedShape.lineSegment,
                )

                is RecognizedShape.RecognizedRay -> dumpRay(
                    ray = recognizedShape.ray,
                )

                is RecognizedShape.RecognizedSpline -> dumpSpline(
                    spline = recognizedShape.spline,
                )
            }
        }
    }

    private fun dumpLineSegment(
        lineSegment: LineSegment,
    ) {
        println("Line segment:")
        println(lineSegment.toReprString())
    }

    private fun dumpRay(
        ray: Ray,
    ) {
        println("Ray:")
        println(ray.toReprString())
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
