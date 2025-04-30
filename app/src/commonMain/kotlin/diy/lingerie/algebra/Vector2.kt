package diy.lingerie.algebra

import diy.lingerie.algebra.NumericObject.Tolerance
import kotlin.math.sqrt

data class Vector2(
    val x: Double,
    val y: Double,
) : NumericObject {
    companion object {
        fun each(a: Double): Vector2 = Vector2(
            x = a,
            y = a,
        )
    }

    val a0: Double
        get() = x

    val a1: Double
        get() = y

    fun dot(
        other: Vector2,
    ): Double = a0 * other.a0 + a1 * other.a1

    fun cross(
        other: Vector2,
    ): Double = a0 * other.a1 - a1 * other.a0

    val length: Double
        get() = sqrt(x * x + y * y)

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector2 -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        else -> true
    }

    operator fun unaryMinus(): Vector2 = Vector2(
        x = -x,
        y = -y,
    )
}
