package dev.toolkt.geometry

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Translation
import dev.toolkt.geometry.transformations.PrimitiveTransformation3D
import dev.toolkt.geometry.transformations.Transformation3D
import dev.toolkt.math.algebra.linear.vectors.Vector3

data class Point3D(
    val pointVector: Vector3,
) : SpatialObject, NumericObject {
    companion object {
        val origin: Point3D = Point3D(
            x = 0.0,
            y = 0.0,
            z = 0.0,
        )

        fun distanceBetween(
            one: Point3D,
            another: Point3D,
        ): Span = Span.Squared(
            valueSquared = (one.pointVector - another.pointVector).magnitudeSquared,
        )

        fun interpolate(
            start: Point3D,
            end: Point3D,
            ratio: Double,
        ): Point3D = Point3D(
            pointVector = start.pointVector + (end.pointVector - start.pointVector) * ratio,
        )

        fun midPoint(
            a: Point3D,
            b: Point3D,
        ): Point3D = Point3D(
            pointVector = a.pointVector + (b.pointVector - a.pointVector) / 2.0,
        )
    }

    constructor(
        x: Double,
        y: Double,
        z: Double,
    ) : this(
        pointVector = Vector3(
            x = x,
            y = y,
            z = z,
        ),
    )

    val x: Double
        get() = pointVector.x

    val y: Double
        get() = pointVector.y

    val z: Double
        get() = pointVector.z

    fun withoutZ(): Point = Point(
        x = x,
        y = y,
    )

    /**
     * Splits the point into a [Point] and a [Double] representing the z-coordinate.
     */
    fun split(): Pair<Point, Double> = Pair(
        first = Point(
            x = x,
            y = y,
        ),
        second = z,
    )

    val xyPlane: Plane
        get() = Plane.of(
            origin = this,
            normalDirection = Direction3.ZAxisPlus,
        )

    fun transformBy(
        transformation: Transformation3D,
    ): Point3D = transformation.transform(point = this)

    fun translationTo(
        target: Point3D,
    ): PrimitiveTransformation3D.Translation = PrimitiveTransformation3D.Translation(
        translationVector = target.pointVector - pointVector,
    )

    fun translateByDistance(
        direction: Direction3,
        distance: Span,
    ): Point3D = transformBy(
        PrimitiveTransformation3D.Translation.inDirection(
            direction = direction,
            distance = distance,
        ),
    )

    fun directionTo(
        other: Point3D,
    ): Direction3 {
        val delta = other.pointVector - pointVector

        return Direction3(
            normalizedDirectionVector = delta.normalize(),
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is Point3D -> false
        !pointVector.equalsWithTolerance(other.pointVector, tolerance = tolerance) -> false
        else -> true
    }

    override fun equalsWithSpatialTolerance(
        other: SpatialObject,
        tolerance: SpatialObject.SpatialTolerance,
    ): Boolean = when {
        other !is Point3D -> false

        else -> distanceBetween(this, other).equalsWithSpatialTolerance(
            Span.Zero,
            tolerance = tolerance,
        )
    }

    override fun toString(): String = toReprString()

    fun toReprString(): String = "Point3D($x, $y, $z)"
}
