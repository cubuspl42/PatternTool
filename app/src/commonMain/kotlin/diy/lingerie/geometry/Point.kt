package diy.lingerie.geometry

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.transformations.Transformation

data class Point(
    val pointVector: Vector2,
) : NumericObject {
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
}
