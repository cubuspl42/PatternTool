package diy.lingerie.algebra.linear.vectors

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.algebra.equalsWithTolerance
import kotlin.math.sqrt

data class Vector2(
    val a0: Double,
    val a1: Double,
) : NumericObject {
    companion object {
        fun full(a: Double): Vector2 = Vector2(
            a0 = a,
            a1 = a,
        )
    }

    val magnitudeSquared: Double
        get() = a0 * a0 + a1 * a1

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    /**
     * @return a normalized vector (potentially non-finite)
     */
    fun normalize(): Vector2 = this / magnitude

    operator fun unaryMinus(): Vector2 = Vector2(
        a0 = -a0,
        a1 = -a1,
    )

    operator fun plus(
        scalar: Double,
    ): Vector2 = Vector2(
        a0 = a0 + scalar,
        a1 = a1 + scalar,
    )

    operator fun minus(
        other: Vector2,
    ): Vector2 = Vector2(
        a0 = a0 - other.a0,
        a1 = a1 - other.a1,
    )

    operator fun times(
        scalar: Double,
    ): Vector2 = Vector2(
        a0 = a0 * scalar,
        a1 = a1 * scalar,
    )

    operator fun div(
        scalar: Double,
    ): Vector2 = Vector2(
        a0 = a0 / scalar,
        a1 = a1 / scalar,
    )

    /**
     * @return a normalized vector (or null if the normalization failed for
     * numeric reasons, including the case of zero or close-to-zero vectors)
     */
    fun normalizeOrNull(): Vector2? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }

    fun dot(
        other: Vector2,
    ): Double = a0 * other.a0 + a1 * other.a1

    fun cross(
        other: Vector2,
    ): Double = a0 * other.a1 - a1 * other.a0

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

operator fun Double.times(
    vector: Vector2,
): Vector2 = vector * this
