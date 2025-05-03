package diy.lingerie.geometry.curves.bezier

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation

data class BezierCurve(
    override val start: Point,
    val firstControl: Point,
    val secondControl: Point,
    override val end: Point,
) : PrimitiveCurve() {
    data class Edge(
        val firstControl: Point,
        val secondControl: Point,
    ) : PrimitiveCurve.Edge() {
        val lastControl: Point
            get() = secondControl

        override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve = BezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )

        override fun transformBy(
            transformation: Transformation,
        ): PrimitiveCurve.Edge = BezierCurve.Edge(
            firstControl = firstControl.transformBy(transformation = transformation),
            secondControl = secondControl.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = when {
            other !is BezierCurve.Edge -> false
            !firstControl.equalsWithTolerance(other.firstControl, tolerance) -> false
            !secondControl.equalsWithTolerance(other.secondControl, tolerance) -> false
            else -> true
        }
    }

    val lastControl: Point
        get() = secondControl

    override val edge: Edge
        get() = BezierCurve.Edge(
            firstControl = firstControl,
            secondControl = secondControl,
        )

    override val subCurves: List<BezierCurve>
        get() = listOf(this)

    override fun transformBy(
        transformation: Transformation,
    ): BezierCurve = BezierCurve(
        start = start.transformBy(transformation = transformation),
        firstControl = firstControl.transformBy(transformation = transformation),
        secondControl = secondControl.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    // TODO: This might not work for degenerate curves
    override val startTangent: Direction?
        get() = start.directionTo(firstControl)

    // TODO: This might not work for degenerate curves
    override val endTangent: Direction?
        get() = secondControl.directionTo(end)

    override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve> {
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
        other !is BezierCurve -> false
        !start.equalsWithTolerance(other.start, tolerance = tolerance) -> false
        !firstControl.equalsWithTolerance(other.firstControl, tolerance = tolerance) -> false
        !secondControl.equalsWithTolerance(other.secondControl, tolerance = tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance = tolerance) -> false
        else -> true
    }
}
