package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.withNextCyclic

data class ClosedSpline<G: SplineContinuity.Positional> private constructor(
    val links: List<Link>,
) : NumericObject {
    data class Link(
        val start: Point,
        val edge: PrimitiveCurve.Edge,
    ) : NumericObject {
        fun bind(
            end: Point,
        ): PrimitiveCurve = edge.bind(
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

        fun transformBy(
            transformation: Transformation,
        ): Link = Link(
            start = start.transformBy(transformation = transformation),
            edge = edge.transformBy(transformation = transformation),
        )
    }

    companion object {
        fun positionallyContinuous(
            links: List<Link>,
        ): ClosedSpline<SplineContinuity.Positional> = ClosedSpline(
            links = links,
        )

        fun fuse(
            edgeCurves: List<PrimitiveCurve>,
        ): ClosedSpline<*> {
            TODO()
        }
    }

    init {
        require(links.isNotEmpty())
    }

    val edgeCurves: List<PrimitiveCurve>
        get() = links.withNextCyclic().map { (link, nextLink) ->
            link.bind(
                end = nextLink.start,
            )
        }

    fun transformBy(
        transformation: Transformation,
    ): ClosedSpline<G> = ClosedSpline(
        links = links.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ClosedSpline<*> -> false
        !links.equalsWithTolerance(other.links, tolerance) -> false
        else -> true
    }
}
