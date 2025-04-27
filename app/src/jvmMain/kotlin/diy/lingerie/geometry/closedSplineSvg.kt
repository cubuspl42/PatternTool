package diy.lingerie.geometry

import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.untrail
import diy.lingerie.utils.xml.svg.asList
import diy.lingerie.utils.xml.svg.asSVGPathSegMovetoAbs
import diy.lingerie.utils.xml.svg.match
import diy.lingerie.utils.xml.svg.p
import diy.lingerie.utils.xml.svg.p1
import diy.lingerie.utils.xml.svg.p2
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

fun SVGPathElement.toClosedSpline(): ClosedSpline {
    val (leadingSvgPathSegs, lastSvgPathSeg) = pathSegList.asList().untrail()!!

    require(lastSvgPathSeg.pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val (firstPathSeg, trailingPathSegs) = leadingSvgPathSegs.uncons()!!

    val originPathSeg =
        firstPathSeg.asSVGPathSegMovetoAbs ?: throw IllegalArgumentException("First path segment is not a MoveTo")
    val originPoint = originPathSeg.p

    val (links, finalPoint) = trailingPathSegs.mapCarrying(
        initialCarry = originPoint,
    ) { start, pathSeg ->
        val (edge, finalPoint) = pathSeg.toCurveEdgeWithFinalPoint()

        Pair(
            ClosedSpline.Link(
                start = start,
                edge = edge,
            ),
            finalPoint,
        )
    }

    if (originPoint != finalPoint) {
        throw IllegalStateException("The path is not closed: $originPoint != $finalPoint")
    }

    return ClosedSpline(
        links = links,
    )
}

private fun SVGPathSeg.toCurveEdgeWithFinalPoint(): Pair<SegmentCurve.Edge, Point> = this.match(
    moveToAbs = {
        throw UnsupportedOperationException()
    },
    lineToAbs = {
        Pair(
            LineSegment.Edge,
            it.p,
        )
    },
    curveToCubicAbs = {
        Pair(
            MonoBezierCurve.Edge(
                firstControl = it.p1,
                secondControl = it.p2,
            ),
            it.p,
        )
    }
)
