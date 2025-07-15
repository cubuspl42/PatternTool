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
        val t = implicitPolynomial.apply(a).valueOrNull ?: return InversionResult.SelfIntersection

        return InversionResult.Specific(t = t)
    }
}
