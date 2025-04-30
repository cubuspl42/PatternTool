package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.withNextCyclic
import diy.lingerie.utils.iterable.withPrevious

/**
 * A composite open curve assumed to be tangent-continuous (G1).
 */
data class OpenSpline private constructor(
    val firstCurve: PrimitiveCurve,
    val sequentialLinks: List<Spline.Link>,
) : OpenCurve(), Spline {
    init {
        require(sequentialLinks.isNotEmpty())
    }

    val sequentialCurves: List<PrimitiveCurve>
        get() {
            val (trailingCurves, _) = sequentialLinks.mapCarrying(
                initialCarry = firstCurve.end,
            ) { start, link ->
                Pair(
                    link.bind(
                        start = start,
                    ),
                    link.end,
                )
            }

            return listOf(firstCurve) + trailingCurves
        }

    override fun transformBy(
        transformation: Transformation,
    ): OpenSpline = OpenSpline(
        firstCurve = firstCurve.transformBy(transformation),
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

    override val links: List<Spline.Link>
        get() = sequentialLinks

    override val start: Point
        get() = TODO("Not yet implemented")

    override val end: Point
        get() = TODO("Not yet implemented")

    override val subCurves: List<PrimitiveCurve>
        get() = sequentialCurves

    override val segmentCurves: List<PrimitiveCurve>
        get() = sequentialCurves
}
