package diy.lingerie.geometry

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.NumericObject.Tolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.geometry.transformations.PrimitiveTransformation.Translation
import diy.lingerie.geometry.transformations.Transformation

data class Point(
    val pointVector: Vector2,
) : SpatialObject, NumericObject {
    companion object {
        fun areCollinear(
            firstPoint: Point,
            secondPoint: Point,
            testPoint: Point,
            tolerance: Tolerance,
        ): Boolean {
            TODO()
        }

        val origin: Point = Point(
            x = 0.0,
            y = 0.0,
        )

        fun distanceBetween(
            one: Point,
            another: Point,
        ): Span = Span(
            valueSquared = (one.pointVector - another.pointVector).magnitudeSquared,
        )
    }

    constructor(
        x: Double,
        y: Double,
    ) : this(
        pointVector = Vector2(
            x = x,
            y = y,
        ),
    )

    val x: Double
        get() = pointVector.x

    val y: Double
        get() = pointVector.y

    fun transformBy(
        transformation: Transformation,
    ): Point = transformation.transform(point = this)

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Point -> false
        !pointVector.equalsWithTolerance(other.pointVector, tolerance = tolerance) -> false
        else -> true
    }

    override fun equalsWithSpatialTolerance(
        other: SpatialObject,
        tolerance: SpatialObject.SpatialTolerance,
    ): Boolean = when {
        other !is Point -> false

        else -> Point.distanceBetween(this, other).equalsWithSpatialTolerance(
            Span.Zero,
            tolerance = tolerance,
        )
    }

    fun translateByDistance(
        direction: Direction,
        distance: Span,
    ): Point {
        TODO()
    }

    fun translationTo(
        target: Point,
    ): Translation = Translation.between(
        origin = this,
        target = target,
    )

    fun reflectedBy(
        mirror: Point,
    ): Point = mirror.transformBy(
        transformation = translationTo(mirror),
    )

    fun directionTo(
        target: Point,
    ): Direction? = translationTo(target = target).direction

    fun castRay(
        direction: Direction,
    ): Ray = Ray.inDirection(
        point = this,
        direction = direction,
    )

    override fun toString(): String = toReprString()

    fun toReprString(): String = "Point($x, $y)"
}
