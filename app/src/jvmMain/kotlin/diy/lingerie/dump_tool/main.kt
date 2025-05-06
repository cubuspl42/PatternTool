package diy.lingerie.dump_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.svg.importSvgPath
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot
import java.nio.file.Path
import kotlin.io.path.reader

enum class DumpStrategyId(val strategy: DumpStrategy) {
    BasisFunctions(DumpBasisFunctionsStrategy), Curves(DumpPrimitiveCurvesStrategy), Spline(DumpSplineStrategy),
}

sealed class DumpStrategy {
    abstract fun dumpSpline(
        spline: Spline,
    )
}

data object DumpSplineStrategy : DumpStrategy() {
    override fun dumpSpline(spline: Spline) {
        println(spline.toReprString())
    }
}

sealed class DumpSegmentCurvesStrategy : DumpStrategy() {
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
        println(curve.basisFunction.toReprString())
    }
}

data object DumpBasisFunctionsStrategy : DumpSegmentCurvesStrategy() {
    override fun dumpSegmentCurve(curve: PrimitiveCurve) {
        println(curve.toReprString())
    }
}

class Tool : CliktCommand() {
    val dumpStrategyId: DumpStrategyId by option("--dump").enum<DumpStrategyId>().required()

    val svgFilePath: Path by argument().path(
        mustExist = true,
        canBeDir = false,
        canBeFile = true,
    ).help("Path to the SVG file")

    override fun run() {
        val svgRoot = SvgRoot.parse(
            reader = svgFilePath.reader(),
        )

        val svgPaths = svgRoot.flatten(
            baseTransformation = Transformation.Identity,
        )

        svgPaths.forEachIndexed { index, svgShape ->
            val svgPath = svgShape as? SvgPath ?: return@forEachIndexed

            val hexColorString = svgPath.stroke.color.toHexString()?.let {
                "[$it]"
            }

            println(
                listOfNotNull("SVG path #${index}", hexColorString).joinToString(" "),
            )

            val spline = Spline.importSvgPath(svgPath = svgPath)

            dumpStrategyId.strategy.dumpSpline(spline)
        }
    }
}

fun main(args: Array<String>) {
    Tool().main(argv = args)
}
