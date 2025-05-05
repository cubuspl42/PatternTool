package diy.lingerie.dump_tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.toSpline
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.simple_dom.svg.SvgRoot
import java.nio.file.Path
import kotlin.io.path.reader

enum class DumpVariant {
    BasisFunctions,
    Curves,
}

class Tool : CliktCommand() {
    val dumpVariant: DumpVariant by option("--dump").enum<DumpVariant>().required()

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

        svgPaths.forEachIndexed { index, svgPath ->
            val hexColor = svgPath.stroke.color.toHexString()
            println("SVG path #${index} [$hexColor]:")

            val spline = svgPath.toSpline()

            dumpSpline(spline = spline)
        }
    }

    private fun dumpSpline(spline: Spline) {
        when(dumpVariant) {
            DumpVariant.BasisFunctions -> spline.segmentCurves.forEachIndexed { index, curve ->
                println("Curve #${index}:")
                println()
                println(curve.basisFunction.toReprString())
                println()
            }

            DumpVariant.Curves -> TODO()
        }
    }
}

fun main(args: Array<String>) {
    Tool().main(argv = args)
}
