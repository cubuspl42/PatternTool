package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation

data class SplineLink(
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
        other !is SplineLink -> false
        !start.equalsWithTolerance(other.start, tolerance) -> false
        !edge.equalsWithTolerance(other.edge, tolerance) -> false
        else -> true
    }

    fun transformBy(
        transformation: Transformation,
    ): SplineLink = SplineLink(
        start = start.transformBy(transformation = transformation),
        edge = edge.transformBy(transformation = transformation),
    )
}
