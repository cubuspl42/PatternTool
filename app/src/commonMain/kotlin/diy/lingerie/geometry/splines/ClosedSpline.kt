package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.utils.iterable.withNextCyclic

data class ClosedSpline(
    val links: List<Link>,
) : NumericObject {
    companion object;

    data class Link(
        val start: Point,
        val edge: SegmentCurve.Edge,
    ) : NumericObject {
        fun bind(
            end: Point,
        ): SegmentCurve = edge.bind(
            start = start,
            end = end,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Link -> false
            !start.equalsWithTolerance(other.start, tolerance) -> false
            !edge.equalsWithTolerance(other.edge, tolerance) -> false
            else -> true
        }
    }

    init {
        require(links.isNotEmpty())
    }

    val edgeCurves: List<SegmentCurve>
        get() = links.withNextCyclic().map { (link, nextLink) ->
            link.bind(
                end = nextLink.start,
            )
        }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ClosedSpline -> false
        !links.equalsWithTolerance(other.links, tolerance) -> false
        else -> true
    }
}

