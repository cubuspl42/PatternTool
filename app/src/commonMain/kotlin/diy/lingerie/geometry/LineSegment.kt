package diy.lingerie.geometry

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation

/**
 * A line segment in the Euclidean plane. Typically, it can be considered a
 * distinguished section of a specific straight line, but if the [start] and
 * the [end] points are the same point, it can be considered a point lying on
 * infinitely many lines.
 */
data class LineSegment(
    override val start: Point,
    override val end: Point,
) : PrimitiveCurve() {
    data object Edge : PrimitiveCurve.Edge() {
        override fun bind(
            start: Point,
            end: Point,
        ): PrimitiveCurve = LineSegment(
            start = start,
            end = end,
        )

        override fun transformBy(transformation: Transformation): Edge = this

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = other == Edge

        override fun toString(): String = "LineSegment.Edge"
    }

    override val edge = Edge

    override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve> {
        TODO("Not yet implemented")
    }

    override fun evaluate(coord: Coord): Point {
        TODO("Not yet implemented")
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is LineSegment -> false
        !start.equalsWithTolerance(other.start, tolerance = tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance = tolerance) -> false
        else -> true
    }

    val containingLine: Line?
        get() = Line.throughPoints(
            point0 = start,
            point1 = end,
        )

    val tangent: Direction?
        get() = start.directionTo(end)

    override fun transformBy(
        transformation: Transformation,
    ): LineSegment = LineSegment(
        start = start.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    override val startTangent: Direction?
        get() = tangent

    override val endTangent: Direction?
        get() = tangent
}
