package diy.lingerie.geometry

import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.utils.awt.toHexString
import diy.lingerie.utils.xml.svg.createPathElement
import diy.lingerie.utils.xml.svg.fill
import diy.lingerie.utils.xml.svg.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import java.awt.Color

fun ClosedSpline.toSvgPathElement(
    document: SVGDocument,
    color: Color = Color.BLACK,
): SVGPathElement {
    val pathElement = document.createPathElement()

    val edgeCurves = this.edgeCurves
    val start = edgeCurves.first().start

    return pathElement.apply {
        pathSegList.apply {
            appendItem(
                createSVGPathSegMovetoAbs(
                    start.x.toFloat(),
                    start.y.toFloat(),
                ),
            )

            edgeCurves.forEach { edgeCurve ->
                appendItem(
                    edgeCurve.toSvgPathSeg(
                        pathElement = pathElement
                    ),
                )
            }
        }

        fill = "none"
        stroke = color.toHexString()
    }
}

private fun SegmentCurve.toSvgPathSeg(
    pathElement: SVGPathElement,
): SVGPathSeg = when (this) {
    is LineSegment -> pathElement.createSVGPathSegLinetoAbs(
        end.x.toFloat(),
        end.y.toFloat(),
    )

    is MonoBezierCurve -> pathElement.createSVGPathSegCurvetoCubicAbs(
        end.x.toFloat(),
        end.y.toFloat(),
        firstControl.x.toFloat(),
        firstControl.y.toFloat(),
        secondControl.x.toFloat(),
        secondControl.y.toFloat(),
    )

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}
