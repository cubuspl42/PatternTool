package diy.lingerie.geometry

import diy.lingerie.geometry.curves.bezier.BezierCurve.Edge
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.splines.OpenSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgPath.Segment
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.takeWhileIsInstanceWithReminder
import diy.lingerie.utils.iterable.uncons

fun SvgPath.toSpline(): Spline {
    val (firstSegment, trailingSegments) = segments.uncons()
        ?: throw IllegalArgumentException("Path must contain at least one segment")

    firstSegment as? Segment.MoveTo ?: throw IllegalArgumentException("Path must start with a MoveTo segment")

    val originPoint = firstSegment.finalPoint

    val (curveSegments, closingSegments) = trailingSegments.takeWhileIsInstanceWithReminder<Segment, Segment.CurveSegment>()

    val closingSegment = closingSegments.firstOrNull()

    if (closingSegment != null && closingSegment !is Segment.ClosePath) {
        // The only acceptable segment after the curve segments is the close segment (or no segment at all)
        // Segments after the closing segment are ignored if present
        throw IllegalArgumentException("Path doesn't close with a ClosePath")
    }

    val (links, finalPoint) = curveSegments.mapCarrying(
        initialCarry = originPoint,
    ) { currentPoint, segment ->
        Pair(
            segment.toLink(),
            segment.finalPoint,
        )
    }

    return when (closingSegment) {
        null -> OpenSpline.OpenSpline(
            origin = originPoint,
            sequentialLinks = links,
        )

        else -> {
            if (originPoint != finalPoint) {
                throw IllegalStateException("The path is not properly closed: $originPoint != $finalPoint")
            }

            return ClosedSpline.positionallyContinuous(
                links = links,
            )
        }
    }
}

fun Segment.CurveSegment.toLink(): Spline.Link {
    val edge = toEdge()

    return edge.semiBind(
        end = finalPoint,
    )
}

fun Segment.CurveSegment.toEdge() = when (this) {
    is Segment.LineTo -> LineSegment.Edge

    is Segment.CubicBezierCurveTo -> Edge(
        firstControl = controlPoint1,
        secondControl = controlPoint2,
    )

    is Segment.SmoothCubicBezierCurveTo -> TODO()
}
