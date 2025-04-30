package diy.lingerie.geometry.curves.bezier

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation

data class MonoBezierCurve(
    override val start: Point,
    override val firstControl: Point,
    val secondControl: Point,
    override val end: Point,
) : BezierCurve() {
    data class Edge(
        override val firstControl: Point,
        val secondControl: Point,
    ) : BezierCurve.Edge() {
        override val joints: List<Joint> = emptyList()

        override val lastControl: Point
            get() = secondControl

        override fun bind(
            start: Point,
            end: Point,
        ): MonoBezierCurve = MonoBezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )

        override fun transformBy(
            transformation: Transformation,
        ): PrimitiveCurve.Edge = MonoBezierCurve.Edge(
            firstControl = firstControl.transformBy(transformation = transformation),
            secondControl = secondControl.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = when {
            other !is MonoBezierCurve.Edge -> false
            !firstControl.equalsWithTolerance(other.firstControl, tolerance) -> false
            !secondControl.equalsWithTolerance(other.secondControl, tolerance) -> false
            else -> true
        }
    }

    override val lastControl: Point
        get() = secondControl

    override val edge: Edge
        get() = MonoBezierCurve.Edge(
            firstControl = firstControl,
            secondControl = secondControl,
        )

    override val joints: List<Joint> = emptyList()

    override val subCurves: List<MonoBezierCurve>
        get() = listOf(this)

    override fun transformBy(
        transformation: Transformation,
    ): MonoBezierCurve = MonoBezierCurve(
        start = start.transformBy(transformation = transformation),
        firstControl = firstControl.transformBy(transformation = transformation),
        secondControl = secondControl.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    override fun splitAt(
        coord: Coord,
    ): Pair<MonoBezierCurve, MonoBezierCurve> {
        TODO("Not yet implemented")
    }

    override fun findOffsetCurve(
        offset: Double,
    ): BezierCurve {
        TODO("Not yet implemented")
    }

    override fun evaluate(coord: Coord): Point {
        TODO("Not yet implemented")
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is MonoBezierCurve -> false
        !start.equalsWithTolerance(other.start, tolerance = tolerance) -> false
        !firstControl.equalsWithTolerance(other.firstControl, tolerance = tolerance) -> false
        !secondControl.equalsWithTolerance(other.secondControl, tolerance = tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance = tolerance) -> false
        else -> true
    }
}
