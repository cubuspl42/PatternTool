package diy.lingerie.geometry.svg

import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.splines.OpenSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgPath.Segment
import diy.lingerie.simple_dom.svg.SvgShape
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.takeWhileIsInstanceWithReminder
import diy.lingerie.utils.iterable.uncons

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
            edgeCurve.toSvgSegment()
        } + listOf(
            SvgPath.Segment.ClosePath,
        ),
    )
}

fun Spline.Companion.importSvgPath(
    svgPath: SvgPath,
): Spline {
    val (firstSegment, trailingSegments) = svgPath.segments.uncons()
        ?: throw IllegalArgumentException("Path must contain at least one segment")

    firstSegment as? Segment.MoveTo ?: throw IllegalArgumentException("Path must start with a MoveTo segment")

    val originPoint = firstSegment.finalPoint

    val (
        curveSegments,
        closingSegments,
    ) = trailingSegments.takeWhileIsInstanceWithReminder<Segment, Segment.CurveSegment>()

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
            Spline.Link.importSvgSegment(segment = segment),
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
