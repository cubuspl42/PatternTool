package diy.lingerie.pattern_tool

import diy.lingerie.geometry.toClosedSpline
import diy.lingerie.simple_dom.svg.toSimplePath
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.documentSvgElement
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement

fun Outline.Companion.loadSvg(
    svgDocument: SVGDocument,
): Outline {
    val singleElement = svgDocument.documentSvgElement.childElements.singleOrNull()
        ?: throw IllegalArgumentException("SVG document must contain a single element")

    val svgPathElement =
        singleElement as? SVGPathElement ?: throw IllegalArgumentException("The single element must be a path element")

    val closedSpline = svgPathElement.toSimplePath().toClosedSpline()

    return Outline.reconstruct(
        closedSpline = closedSpline,
        edgeMetadata = Outline.EdgeMetadata(
            seamAllowance = SeamAllowance(
                allowanceMm = 6.0,
            ),
        ),
    )
}
