package dev.toolkt.geometry.svg

import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.BezierCurve.Edge
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
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

fun QuadraticBezierBinomial.toSvgPath(
    stroke: SvgShape.Stroke = SvgShape.Stroke.default,
): SvgPath = SvgPath(
    stroke = stroke,
    segments = listOf(
        SvgPath.Segment.MoveTo(
            targetPoint = Point(pointVector = point0),
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

fun QuadraticBezierBinomial.toSvgBezierSegment(): SvgPath.Segment.QuadraticBezierCurveTo =
    SvgPath.Segment.QuadraticBezierCurveTo(
        controlPoint = Point(pointVector = point1),
        finalPoint = Point(pointVector = point2),
    )

fun PrimitiveCurve.Edge.Companion.importSvgSegment(
    segment: SvgPath.Segment.CurveSegment,
): PrimitiveCurve.Edge = when (segment) {
    is Segment.LineTo -> LineSegment.Edge

    is Segment.QuadraticBezierCurveTo -> TODO()

    is Segment.SmoothQuadraticBezierCurveTo -> TODO()

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
