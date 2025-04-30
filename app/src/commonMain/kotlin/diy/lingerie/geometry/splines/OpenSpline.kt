package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.withNextCyclic

/**
 * A composite open curve assumed to be tangent-continuous (G1).
 */
data class OpenSpline private constructor(
    val sequentialLinks: List<SplineLink>,
) : OpenCurve(), Spline {
    init {
        require(sequentialLinks.isNotEmpty())
    }

    val sequentialCurves: List<PrimitiveCurve>
        get() = sequentialLinks.withNextCyclic().map { (link, nextLink) ->
            link.bind(
                end = nextLink.start,
            )
        }

    fun transformBy(
        transformation: Transformation,
    ): OpenSpline = OpenSpline(
        sequentialLinks = sequentialLinks.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is OpenSpline -> false
        !sequentialLinks.equalsWithTolerance(other.links, tolerance) -> false
        else -> true
    }

    override val links: List<SplineLink>
        get() = sequentialLinks

    override val subCurves: List<PrimitiveCurve>
        get() = sequentialCurves

    override val segmentCurves: List<PrimitiveCurve>
        get() = sequentialCurves
}
