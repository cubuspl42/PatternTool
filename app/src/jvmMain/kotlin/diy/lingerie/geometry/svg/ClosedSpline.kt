package dev.toolkt.geometry.svg

import dev.toolkt.geometry.splines.ClosedSpline
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.splines.Spline
import diy.lingerie.simple_dom.svg.PureSvgPath
import diy.lingerie.simple_dom.svg.PureSvgPath.Segment
import diy.lingerie.simple_dom.svg.PureSvgShape
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.core.iterable.takeWhileIsInstanceWithReminder
import dev.toolkt.core.iterable.uncons

fun ClosedSpline.toSvgPath(
    stroke: PureSvgShape.Stroke = PureSvgShape.Stroke.default,
): PureSvgPath {
    val edgeCurves = this.cyclicCurves
    val start = edgeCurves.first().start

    return PureSvgPath(
        stroke = stroke,
        segments = listOf(
            PureSvgPath.Segment.MoveTo(
                targetPoint = start,
            ),
        ) + edgeCurves.map { edgeCurve ->
            edgeCurve.toSvgSegment()
        } + listOf(
            PureSvgPath.Segment.ClosePath,
        ),
    )
}

fun Spline.Companion.importSvgPath(
    svgPath: PureSvgPath,
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
