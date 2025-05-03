package diy.lingerie.geometry

import diy.lingerie.geometry.curves.bezier.BezierCurve.Edge
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
        ?: throw IllegalArgumentException("Path must contain a closing segment")

    if (lastSegment != SvgPath.Segment.ClosePath) {
        throw IllegalArgumentException("Path must end with a ClosePath segment")
    }

    val (links, finalPoint) = innerSegments.mapCarrying(
        initialCarry = originPoint,
    ) { currentPoint, segment ->
        segment as? SvgPath.Segment.CurveSegment
            ?: throw IllegalArgumentException("Each inner path segment must be a curve segment")

        Pair(
            segment.toLink(),
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

fun SvgPath.Segment.CurveSegment.toLink(): Spline.Link {
    val edge = toEdge()

    return edge.semiBind(
        end = finalPoint,
    )
}

fun SvgPath.Segment.CurveSegment.toEdge() = when (this) {
    is SvgPath.Segment.LineTo -> LineSegment.Edge

    is SvgPath.Segment.CubicBezierCurveTo -> Edge(
        firstControl = controlPoint1,
        secondControl = controlPoint2,
    )

    is SvgPath.Segment.SmoothCubicBezierCurveTo -> TODO()
}
