package diy.lingerie.geometry.splines

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.utils.iterable.withNextCyclic

data class ClosedSpline(
    val links: List<Link>,
) {
    companion object;

    data class Link(
        val start: Point,
        val edge: SegmentCurve.Edge,
    ) {
        fun bind(
            end: Point,
        ): SegmentCurve = edge.bind(
            start = start,
            end = end,
        )
    }

    val edgeCurves: List<SegmentCurve>
        get() = links.withNextCyclic().map { (link, nextLink) ->
            link.bind(
                end = nextLink.start,
            )
        }
}
