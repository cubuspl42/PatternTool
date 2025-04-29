package diy.lingerie.geometry

import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.simple_dom.svg.SvgPath

fun ClosedSpline.toSvgPath(
    stroke: SvgPath.Stroke = SvgPath.Stroke.default,
): SvgPath {
    val edgeCurves = this.edgeCurves
    val start = edgeCurves.first().start

    return SvgPath(
        stroke = stroke,
        segments = listOf(
            SvgPath.Segment.MoveTo(
                targetPoint = start,
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
