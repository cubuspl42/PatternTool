package diy.lingerie.algebra.polynomials

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance

data class ConstantPolynomial(
    override val a0: Double,
) : SubLinearPolynomial() {
    companion object {
        val zero: ConstantPolynomial = ConstantPolynomial(
            a0 = 0.0,
        )
    }

    override val coefficients: List<Double>
        get() = listOf(
            a0,
        )

    override fun divide(
        x0: Double,
    ): Pair<Polynomial, Double> = Pair(ConstantPolynomial.zero, a0)

    override fun findRootsAnalytically(): List<Double> = emptyList()

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ConstantPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        else -> true
    }

    override fun apply(x: Double): Double = a0
}
