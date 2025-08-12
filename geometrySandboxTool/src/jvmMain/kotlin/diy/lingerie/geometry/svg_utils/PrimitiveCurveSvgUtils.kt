package diy.lingerie.geometry.svg_utils

import dev.toolkt.dom.pure.svg.PureSvgPath
import dev.toolkt.dom.pure.svg.PureSvgPath.Segment
import dev.toolkt.dom.pure.svg.PureSvgShape
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.BezierCurve.Edge
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import dev.toolkt.geometry.splines.Spline

fun BezierCurve.toSvgPath(
    stroke:
    PureSvgShape.Stroke = PureSvgShape.Stroke.default,
): PureSvgPath = PureSvgPath(
    stroke = stroke,
    segments = listOf(
        Segment.MoveTo(
            targetPoint = start,
        ),
    ) + toSvgBezierSegment(),
)

fun QuadraticBezierBinomial.toSvgPath(
    stroke: PureSvgShape.Stroke = PureSvgShape.Stroke.default,
): PureSvgPath = PureSvgPath(
    stroke = stroke,
    segments = listOf(
        Segment.MoveTo(
            targetPoint = Point(pointVector = point0),
        ),
    ) + toSvgBezierSegment(),
)

fun PrimitiveCurve.toSvgSegment(): Segment = when (this) {
    is LineSegment -> Segment.LineTo(
        finalPoint = end,
    )

    is BezierCurve -> toSvgBezierSegment()

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

fun BezierCurve.toSvgBezierSegment(): Segment.CubicBezierCurveTo = Segment.CubicBezierCurveTo(
    controlPoint1 = firstControl,
    controlPoint2 = secondControl,
    finalPoint = end,
)

fun QuadraticBezierBinomial.toSvgBezierSegment(): Segment.QuadraticBezierCurveTo =
    Segment.QuadraticBezierCurveTo(
        controlPoint = Point(pointVector = point1),
        finalPoint = Point(pointVector = point2),
    )

fun PrimitiveCurve.Edge.Companion.importSvgSegment(
    segment: Segment.CurveSegment,
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
    segment: Segment.CurveSegment,
): Spline.Link {
    val edge = PrimitiveCurve.Edge.importSvgSegment(segment)

    return edge.semiBind(
        end = segment.finalPoint,
    )
}
