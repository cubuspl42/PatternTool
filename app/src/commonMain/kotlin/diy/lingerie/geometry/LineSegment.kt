package diy.lingerie.geometry

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.NumericObject.Tolerance
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricCurveFunction
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricLineFunction

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
    override val basisFunction = ParametricLineFunction(
        s = start.pointVector,
        d = end.pointVector - start.pointVector,
    )

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

        override fun toReprString(): String = "LineSegment.Edge"
    }

    override val edge = Edge

    override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve> {
        TODO("Not yet implemented")
    }

    override fun findOffsetCurve(
        offset: Double,
    ): LineSegment {
        val normal =
            this.normal ?: throw IllegalStateException("Cannot find offset curve of a line segment with no normal")

        return transformBy(
            transformation = PrimitiveTransformation.Translation.inDirection(
                direction = normal,
                distance = Span.of(value = offset),
            )
        )
    }

    override fun findBoundingBox(): BoundingBox = BoundingBox.of(
        pointA = start,
        pointB = end,
    )

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

    val normal: Direction?
        get() = tangent?.normal

    override val tangentDirection = FeatureFunction.constant(tangent)

    override fun transformBy(
        transformation: Transformation,
    ): LineSegment = LineSegment(
        start = start.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    override fun toReprString(): String {
        return """
            |LineSegment(
            |  start = ${start.toReprString()},
            |  end = ${end.toReprString()},
            |)
        """.trimMargin()
    }

    override val startTangent: Direction?
        get() = tangent

    override val endTangent: Direction?
        get() = tangent
}
