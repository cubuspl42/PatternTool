package dev.toolkt.geometry.svg

import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.BezierCurve.Edge
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import diy.lingerie.simple_dom.svg.PureSvgPath
import diy.lingerie.simple_dom.svg.PureSvgPath.Segment
import diy.lingerie.simple_dom.svg.PureSvgShape

fun BezierCurve.toSvgPath(
    stroke: PureSvgShape.Stroke = PureSvgShape.Stroke.default,
): PureSvgPath = PureSvgPath(
    stroke = stroke,
    segments = listOf(
        PureSvgPath.Segment.MoveTo(
            targetPoint = start,
        ),
    ) + toSvgBezierSegment(),
)

fun QuadraticBezierBinomial.toSvgPath(
    stroke: PureSvgShape.Stroke = PureSvgShape.Stroke.default,
): PureSvgPath = PureSvgPath(
    stroke = stroke,
    segments = listOf(
        PureSvgPath.Segment.MoveTo(
            targetPoint = Point(pointVector = point0),
        ),
    ) + toSvgBezierSegment(),
)

fun PrimitiveCurve.toSvgSegment(): PureSvgPath.Segment = when (this) {
    is LineSegment -> PureSvgPath.Segment.LineTo(
        finalPoint = end,
    )

    is BezierCurve -> toSvgBezierSegment()

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

fun BezierCurve.toSvgBezierSegment(): PureSvgPath.Segment.CubicBezierCurveTo = PureSvgPath.Segment.CubicBezierCurveTo(
    controlPoint1 = firstControl,
    controlPoint2 = secondControl,
    finalPoint = end,
)

fun QuadraticBezierBinomial.toSvgBezierSegment(): PureSvgPath.Segment.QuadraticBezierCurveTo =
    PureSvgPath.Segment.QuadraticBezierCurveTo(
        controlPoint = Point(pointVector = point1),
        finalPoint = Point(pointVector = point2),
    )

fun PrimitiveCurve.Edge.Companion.importSvgSegment(
    segment: PureSvgPath.Segment.CurveSegment,
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
    segment: PureSvgPath.Segment.CurveSegment,
): Spline.Link {
    val edge = PrimitiveCurve.Edge.importSvgSegment(segment)

    return edge.semiBind(
        end = segment.finalPoint,
    )
}
