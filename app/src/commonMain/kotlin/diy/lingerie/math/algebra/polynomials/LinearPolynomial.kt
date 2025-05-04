package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance

data class LinearPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
) : SubQuadraticPolynomial(), SuperConstantPolynomial {
    companion object {
        fun normalized(
            a0: Double,
            a1: Double,
        ): SubQuadraticPolynomial = when (a1) {
            0.0 -> ConstantPolynomial(
                a0 = a0,
            )

            else -> LinearPolynomial(
                a0 = a0,
                a1 = a1,
            )
        }
    }

    override val coefficients: List<Double>
        get() = listOf(
            a0,
            a1,
        )

    override fun findRootsAnalytically(): List<Double> = listOf(findRoot())

    fun findRoot(): Double = -a0 / a1

    override fun apply(
        x: Double,
    ): Double = a0 + a1 * x

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is LinearPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        else -> true
    }
}
