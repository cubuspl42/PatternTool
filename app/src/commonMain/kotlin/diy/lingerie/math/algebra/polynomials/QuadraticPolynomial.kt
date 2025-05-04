package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import kotlin.math.sqrt

data class QuadraticPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
    override val a2: Double,
) : SubCubicPolynomial(), SuperLinearPolynomial {
    companion object {
        fun normalized(
            a0: Double,
            a1: Double,
            a2: Double,
        ): SubCubicPolynomial = when (a2) {
            0.0 -> LinearPolynomial.normalized(
                a0 = a0,
                a1 = a1,
            )

            else -> QuadraticPolynomial(
                a0 = a0,
                a1 = a1,
                a2 = a2,
            )
        }
    }

    operator fun plus(
        other: SubQuadraticPolynomial,
    ): QuadraticPolynomial = QuadraticPolynomial(
        a0 = this.a0 + other.a0,
        a1 = this.a1 + (other.a1 ?: 0.0),
        a2 = this.a2,
    )

    override val coefficients: List<Double>
        get() = listOf(
            a0,
            a1,
            a2,
        )

    override fun findRootsAnalytically(): List<Double> = findRoots()?.toList() ?: emptyList()

    fun findRoots(): Pair<Double, Double>? {
        val a = a2
        val b = a1
        val c = a0

        val discriminant: Double = b * b - 4 * a * c

        fun buildRoot(
            sign: Double,
        ): Double = (-b + sign * sqrt(discriminant)) / (2 * a)

        return when {
            discriminant >= 0 -> Pair(
                buildRoot(sign = -1.0),
                buildRoot(sign = 1.0),
            )

            else -> null
        }
    }

    override fun apply(x: Double): Double = a0 + a1 * x + a2 * x * x

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is QuadraticPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        else -> true
    }
}
