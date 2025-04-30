package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.withNextCyclic

/**
 * A composite closed curve guaranteed only to be positionally-continuous (C0).
 */
data class ClosedSpline<G: SplineContinuity.Positional> private constructor(
    val cyclicLinks: List<SplineLink>,
) : NumericObject {
    companion object {
        fun positionallyContinuous(
            links: List<SplineLink>,
        ): ClosedSpline<SplineContinuity.Positional> = ClosedSpline(
            cyclicLinks = links,
        )

        fun fuse(
            edgeCurves: List<PrimitiveCurve>,
        ): ClosedSpline<*> {
            TODO()
        }
    }

    init {
        require(cyclicLinks.isNotEmpty())
    }

    val cyclicEdgeCurves: List<PrimitiveCurve>
        get() = cyclicLinks.withNextCyclic().map { (link, nextLink) ->
            link.bind(
                end = nextLink.start,
            )
        }

    fun transformBy(
        transformation: Transformation,
    ): ClosedSpline<G> = ClosedSpline(
        cyclicLinks = cyclicLinks.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ClosedSpline<*> -> false
        !cyclicLinks.equalsWithTolerance(other.cyclicLinks, tolerance) -> false
        else -> true
    }
}
