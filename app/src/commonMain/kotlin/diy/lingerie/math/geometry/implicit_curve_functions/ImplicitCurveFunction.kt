package diy.lingerie.math.geometry.implicit_curve_functions

import diy.lingerie.math.algebra.Function
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.ConstantPolynomial
import diy.lingerie.math.algebra.polynomials.Polynomial
import diy.lingerie.math.algebra.polynomials.plus
import diy.lingerie.math.geometry.ParametricPolynomial

/**
 * A bivariate polynomial in variables x and y, modelling a curve
 */
sealed class ImplicitCurveFunction : Function<Vector2, Double>, NumericObject {
    protected operator fun Polynomial.plus(
        constant: Double,
    ) = this + ConstantPolynomial(constant)

    abstract fun substitute(
        parametricPolynomial: ParametricPolynomial,
    ): Polynomial
}
