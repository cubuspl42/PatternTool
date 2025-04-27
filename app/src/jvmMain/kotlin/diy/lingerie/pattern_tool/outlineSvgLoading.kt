package diy.lingerie.pattern_tool

import diy.lingerie.geometry.toClosedSpline
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot

fun Outline.Companion.loadSvg(
    svgRoot: SvgRoot,
): Outline {
    val singleElement = svgRoot.children.singleOrNull()
        ?: throw IllegalArgumentException("SVG document must contain a single element")

    val svgPath =
        singleElement as? SvgPath ?: throw IllegalArgumentException("The single element must be a path element")

    val closedSpline = svgPath.toClosedSpline()

    return Outline.reconstruct(
        closedSpline = closedSpline,
        edgeMetadata = Outline.EdgeMetadata(
            seamAllowance = SeamAllowance(
                allowanceMm = 6.0,
            ),
        ),
    )
}
