package diy.lingerie.algebra

import diy.lingerie.algebra.NumericObject.Tolerance

data class Vector2(
    val x: Double,
    val y: Double,
) : NumericObject {
    val a0: Double
        get() = x

    val a1: Double
        get() = y

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector2 -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        else -> true
    }
}
