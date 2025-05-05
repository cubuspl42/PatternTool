package diy.lingerie.geometry

import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgShape
import kotlin.collections.plus

fun ClosedSpline.toSvgPath(
    stroke: SvgShape.Stroke = SvgShape.Stroke.default,
): SvgPath {
    val edgeCurves = this.cyclicCurves
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

fun PrimitiveCurve.toSvgPathSeg(): SvgPath.Segment = when (this) {
    is LineSegment -> SvgPath.Segment.LineTo(
        finalPoint = end,
    )

    is BezierCurve -> toBezierPathSegment()

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

fun BezierCurve.toSvgPath(
    stroke: SvgShape.Stroke = SvgShape.Stroke.default,
): SvgPath = SvgPath(
    stroke = stroke,
    segments = listOf(
        SvgPath.Segment.MoveTo(
            targetPoint = start,
        ),
    ) + toBezierPathSegment(),
)

fun BezierCurve.toBezierPathSegment(): SvgPath.Segment.CubicBezierCurveTo = SvgPath.Segment.CubicBezierCurveTo(
    controlPoint1 = firstControl,
    controlPoint2 = secondControl,
    finalPoint = end,
)
