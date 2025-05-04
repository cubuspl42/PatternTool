package diy.lingerie.algebra.linear.vectors

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.algebra.equalsWithTolerance
import kotlin.math.sqrt

data class Vector3(
    val a0: Double,
    val a1: Double,
    val a2: Double,
) : NumericObject {
    companion object {
        fun full(
            a: Double,
        ): Vector3 = Vector3(
            a0 = a,
            a1 = a,
            a2 = a,
        )
    }

    val magnitudeSquared: Double
        get() = a0 * a0 + a1 * a1 + a2 * a2

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    fun normalize(): Vector3 = this / magnitude

    operator fun unaryMinus(): Vector3 = Vector3(
        a0 = -a0,
        a1 = -a1,
        a2 = -a2,
    )

    operator fun plus(
        scalar: Double,
    ): Vector3 = Vector3(
        a0 = a0 + scalar,
        a1 = a1 + scalar,
        a2 = a2 + scalar,
    )

    operator fun minus(
        other: Vector3,
    ): Vector3 = Vector3(
        a0 = a0 - other.a0,
        a1 = a1 - other.a1,
        a2 = a2 - other.a2,
    )

    operator fun times(
        scalar: Double,
    ): Vector3 = Vector3(
        a0 = a0 * scalar,
        a1 = a1 * scalar,
        a2 = a2 * scalar,
    )

    operator fun div(
        scalar: Double,
    ): Vector3 = Vector3(
        a0 = a0 / scalar,
        a1 = a1 / scalar,
        a2 = a2 / scalar,
    )

    fun normalizeOrNull(): Vector3? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }

    fun dot(
        other: Vector3,
    ): Double = a0 * other.a0 + a1 * other.a1 + a2 * other.a2

    fun cross(
        other: Vector3,
    ): Vector3 = Vector3(
        a0 = a1 * other.a2 - a2 * other.a1,
        a1 = a2 * other.a0 - a0 * other.a2,
        a2 = a0 * other.a1 - a1 * other.a0,
    )

    fun toList(): List<Double> = listOf(
        a0,
        a1,
        a2,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector3 -> false

        !a0.equalsWithTolerance(
            other = other.a0,
            tolerance = tolerance,
        ) -> false

        !a1.equalsWithTolerance(
            other = other.a1,
            tolerance = tolerance,
        ) -> false

        !a2.equalsWithTolerance(
            other = other.a2,
            tolerance = tolerance,
        ) -> false

        else -> true
    }
}

operator fun Double.times(
    vector: Vector3,
): Vector3 = vector * this
