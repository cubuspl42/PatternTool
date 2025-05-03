package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.clusterSimilar
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.withNextCyclic
import diy.lingerie.utils.iterable.withPrevious
import diy.lingerie.utils.iterable.withPreviousCyclic

/**
 * A composite closed curve guaranteed only to be positionally-continuous (C0).
 */
data class ClosedSpline(
    val cyclicLinks: List<Spline.Link>,
) : Spline, NumericObject {
    companion object {
        fun positionallyContinuous(
            links: List<Spline.Link>,
        ): ClosedSpline = ClosedSpline(
            cyclicLinks = links,
        )

        /**
         * @param cyclicCurves a list of curves, where each curves starts at
         * the end of the previous one, and ends at the start of the next one
         * (cyclically).
         */
        fun connect(
            cyclicCurves: List<OpenCurve>,
        ): ClosedSpline {
            val primitiveCyclicCurves = cyclicCurves.flatMap { it.subCurves }

            return ClosedSpline(
                cyclicLinks = primitiveCyclicCurves.withPreviousCyclic().map { (prevCurve, curve) ->
                    Spline.Link.connect(
                        prevCurve = prevCurve,
                        curve = curve,
                    )
                },
            )
        }

        /**
         * @param separatedCurves a list of curves, where each curve might be
         * separated from (or even intersect) the previous/next curve.
         */
        fun interconnect(
            separatedCurves: List<OpenCurve>,
        ): ClosedSpline = ClosedSpline.connect(
            cyclicCurves = separatedCurves.withNextCyclic().flatMap { (curve, nextCurve) ->
                listOf(curve) + LineSegment(
                    start = curve.end,
                    end = nextCurve.start,
                )
            },
        )
    }

    init {
        require(cyclicLinks.isNotEmpty())
    }

    val cyclicCurves: List<PrimitiveCurve>
        get() = cyclicLinks.withPreviousCyclic().map { (prevLink, link) ->
            link.bind(
                start = prevLink.end,
            )
        }

    val smoothSubSplines: List<OpenSpline>
        get() = cyclicCurves.clusterSimilar { prevCurve, nextCurve ->
            prevCurve.connectsSmoothly(
                nextCurve = nextCurve,
            )
        }.map { smoothSequentialCurves ->
            OpenSpline.connect(
                sequentialCurves = smoothSequentialCurves,
            )
        }

    fun transformBy(
        transformation: Transformation,
    ): ClosedSpline = ClosedSpline(
        cyclicLinks = cyclicLinks.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ClosedSpline -> false
        !cyclicLinks.equalsWithTolerance(other.cyclicLinks, tolerance) -> false
        else -> true
    }

    override val links: List<Spline.Link>
        get() = cyclicLinks

    override val segmentCurves: List<PrimitiveCurve>
        get() = cyclicCurves
}
