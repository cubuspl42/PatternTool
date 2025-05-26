package diy.lingerie.math.geometry.implicit_curve_functions

import diy.lingerie.math.algebra.Function
import dev.toolkt.core.numeric.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.ConstantPolynomial
import diy.lingerie.math.algebra.polynomials.Polynomial
import diy.lingerie.math.algebra.polynomials.plus
import diy.lingerie.math.geometry.ParametricPolynomial

/**
 * A bivariate polynomial in variables x and y, modelling a curve. For points
 * on the curve, the polynomial evaluates to zero. For points not on the curve,
 * the polynomial evaluates to a non-zero value which indicates on which side
 * of the curve the point lies.
 */
sealed class ImplicitCurveFunction : Function<Vector2, Double>, NumericObject {
    protected operator fun Polynomial.plus(
        constant: Double,
    ) = this + ConstantPolynomial(constant)

    abstract fun substitute(
        parametricPolynomial: ParametricPolynomial<*>,
    ): Polynomial
}
