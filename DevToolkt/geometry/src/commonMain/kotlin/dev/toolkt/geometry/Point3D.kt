package dev.toolkt.geometry

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
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
//
//    fun transformBy(
//        transformation: Transformation,
//    ): Point3D = transformation.transform(point = this)

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
