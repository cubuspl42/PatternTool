package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.geometry.math.RationalImplicitPolynomial
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction
import dev.toolkt.math.algebra.linear.vectors.Vector2

class InvertedProperCubicBezierBinomial(
    // TODO: Merge into this class?
    private val implicitPolynomial: RationalImplicitPolynomial,
) : ParametricCurveFunction.InvertedCurveFunction() {
    override fun apply(
        a: Vector2,
    ): InversionResult {
        val ratio = implicitPolynomial.apply(a)

        val t = ratio.valueOrNull ?: return InversionResult.SelfIntersection

        return InversionResult.Specific(t = t)
    }
}
