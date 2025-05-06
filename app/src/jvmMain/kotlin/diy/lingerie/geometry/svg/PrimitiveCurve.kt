package diy.lingerie.geometry.svg

import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve.Edge
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgPath.Segment
import diy.lingerie.simple_dom.svg.SvgShape

fun BezierCurve.toSvgPath(
    stroke: SvgShape.Stroke = SvgShape.Stroke.default,
): SvgPath = SvgPath(
    stroke = stroke,
    segments = listOf(
        SvgPath.Segment.MoveTo(
            targetPoint = start,
        ),
    ) + toSvgBezierSegment(),
)

fun PrimitiveCurve.toSvgSegment(): SvgPath.Segment = when (this) {
    is LineSegment -> SvgPath.Segment.LineTo(
        finalPoint = end,
    )

    is BezierCurve -> toSvgBezierSegment()

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

fun BezierCurve.toSvgBezierSegment(): SvgPath.Segment.CubicBezierCurveTo = SvgPath.Segment.CubicBezierCurveTo(
    controlPoint1 = firstControl,
    controlPoint2 = secondControl,
    finalPoint = end,
)

fun PrimitiveCurve.Edge.Companion.importSvgSegment(
    segment: SvgPath.Segment.CurveSegment,
): PrimitiveCurve.Edge = when (segment) {
    is Segment.LineTo -> LineSegment.Edge

    is Segment.CubicBezierCurveTo -> Edge(
        firstControl = segment.controlPoint1,
        secondControl = segment.controlPoint2,
    )

    is Segment.SmoothCubicBezierCurveTo -> TODO()
}

fun Spline.Link.Companion.importSvgSegment(
    segment: SvgPath.Segment.CurveSegment,
): Spline.Link {
    val edge = PrimitiveCurve.Edge.importSvgSegment(segment)

    return edge.semiBind(
        end = segment.finalPoint,
    )
}
