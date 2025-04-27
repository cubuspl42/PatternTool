package diy.lingerie.geometry

import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.utils.awt.toHexString
import diy.lingerie.utils.xml.svg.createPathElement
import diy.lingerie.utils.xml.svg.fill
import diy.lingerie.utils.xml.svg.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import java.awt.Color

fun ClosedSpline.toSvgPathElement(
    color: SimpleColor = SimpleColor.black,
): SvgPath {
    val edgeCurves = this.edgeCurves
    val start = edgeCurves.first().start

    return SvgPath(
        strokeColor = color,
        segments = listOf(
            SvgPath.Segment.MoveTo(
                finalPoint = start,
            ),
        ) + edgeCurves.map { edgeCurve ->
            edgeCurve.toSvgPathSeg()
        } + listOf(
            SvgPath.Segment.ClosePath,
        ),
    )
}

private fun SegmentCurve.toSvgPathSeg(): SvgPath.Segment = when (this) {
    is LineSegment -> SvgPath.Segment.LineTo(
        finalPoint = end,
    )

    is MonoBezierCurve -> SvgPath.Segment.CubicBezierCurveTo(
        controlPoint1 = firstControl,
        controlPoint2 = secondControl,
        finalPoint = end,
    )

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}
