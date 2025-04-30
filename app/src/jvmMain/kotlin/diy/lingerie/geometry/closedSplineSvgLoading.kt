package diy.lingerie.geometry

import diy.lingerie.geometry.curves.bezier.MonoBezierCurve.Edge
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.untrail

fun SvgPath.toClosedSpline(): ClosedSpline {
    val (firstSegment, trailingSegments) = segments.uncons()
        ?: throw IllegalArgumentException("Path must contain at least one segment")

    firstSegment as? SvgPath.Segment.MoveTo ?: throw IllegalArgumentException("Path must start with a MoveTo segment")

    val originPoint = firstSegment.finalPoint

    val (innerSegments, lastSegment) = trailingSegments.untrail()
        ?: throw IllegalArgumentException("Path must contain at least two segments")

    if (lastSegment != SvgPath.Segment.ClosePath) {
        throw IllegalArgumentException("Path must end with a ClosePath segment")
    }

    val (links, finalPoint) = innerSegments.mapCarrying(
        initialCarry = originPoint,
    ) { start, segment ->
        segment as? SvgPath.Segment.CurveSegment
            ?: throw IllegalArgumentException("Each inner path segment must be a curve segment")

        val edge = when (segment) {
            is SvgPath.Segment.LineTo -> LineSegment.Edge

            is SvgPath.Segment.CubicBezierCurveTo -> Edge(
                firstControl = segment.controlPoint1,
                secondControl = segment.controlPoint2,
            )

            is SvgPath.Segment.SmoothCubicBezierCurveTo -> TODO()
        }

        Pair(
            Spline.Link(
                start = start,
                edge = edge,
            ),
            segment.finalPoint,
        )
    }

    if (originPoint != finalPoint) {
        throw IllegalStateException("The path is not closed: $originPoint != $finalPoint")
    }

    return ClosedSpline.positionallyContinuous(
        links = links,
    )
}
