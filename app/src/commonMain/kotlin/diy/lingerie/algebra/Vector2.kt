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

    val magnitudeSquared: Double
        get() = x * x + y * y

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

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

    operator fun minus(
        other: Vector2,
    ): Vector2 = Vector2(
        x = a0 - other.a0,
        y = a1 - other.a1,
    )

    operator fun div(
        scalar: Double,
    ): Vector2 = Vector2(
        x = a0 / scalar,
        y = a1 / scalar,
    )

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    /**
     * @return a normalized vector (potentially non-finite)
     */
    fun normalize(): Vector2 = this / magnitude

    /**
     * @return a normalized vector (or null if the normalization failed for
     * numeric reasons, including the case of zero or close-to-zero vectors)
     */
    fun normalizeOrNull(): Vector2? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }
}
