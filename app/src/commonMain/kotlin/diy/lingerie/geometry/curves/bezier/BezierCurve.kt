package diy.lingerie.geometry.curves.bezier

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation

abstract class BezierCurve : PrimitiveCurve() {
    data class Joint(
        val rearControl: Point,
        val coord: Coord,
        val frontControl: Point,
    ) : NumericObject {
        val position: Point
            get() = TODO()

        fun transformBy(
            transformation: Transformation,
        ): Joint = Joint(
            frontControl = frontControl.transformBy(transformation),
            coord = coord,
            rearControl = rearControl.transformBy(transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = when {
            other !is Joint -> false
            !rearControl.equalsWithTolerance(other.rearControl, tolerance) -> false
            !coord.equalsWithTolerance(other.coord, tolerance) -> false
            !frontControl.equalsWithTolerance(other.frontControl, tolerance) -> false
            else -> true
        }

    }

    abstract class Edge : PrimitiveCurve.Edge() {
        abstract val firstControl: Point

        abstract val joints: List<Joint>

        abstract val lastControl: Point

        abstract override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve
    }

    abstract override val edge: BezierCurve.Edge

    abstract val firstControl: Point

    abstract val joints: List<Joint>

    abstract val lastControl: Point

    abstract val subCurves: List<MonoBezierCurve>

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve>

    abstract fun findOffsetCurve(
        offset: Double,
    ): BezierCurve
}
