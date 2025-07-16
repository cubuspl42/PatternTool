package dev.toolkt.geometry.math.implicit_curve_functions

import dev.toolkt.math.algebra.Function
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.plus

/**
 * A bivariate polynomial in variables x and y, modelling a curve in the form f : (p: ℝ²) → ℝ.
 * Given a point, it returns a value indicating whether the point lies on the curve.
 *
 * For points on the curve, this function evaluates to zero (within reasonable
 * tolerance). For points not on the curve, the polynomial evaluates to a non-zero
 * value which indicates on which side of the curve the point lies (unless the
 * curve is a point, in which case it doesn't really _have_ sides).
 */
sealed class ImplicitCurveFunction : Function<Vector2, Double>, NumericObject {
    protected operator fun Polynomial.plus(
        constant: Double,
    ) = this + ConstantPolynomial(constant)

    abstract fun substitute(
        parametricPolynomial: ParametricPolynomial<*>,
    ): Polynomial
}
