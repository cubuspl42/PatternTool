package dev.toolkt.geometry.math.implicit_curve_functions

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial

data class ImplicitConstFunction(
    val a: Double,
) : ImplicitCurveFunction() {
    companion object {
        val Zero = ImplicitConstFunction(0.0)
    }

    override fun substitute(
        parametricPolynomial: ParametricPolynomial<*>,
    ): Polynomial = ConstantPolynomial(a0 = this.a)

    override fun findXDerivative(): ImplicitCurveFunction = Zero

    override fun findYDerivative(): ImplicitCurveFunction = Zero

    override fun apply(a: Vector2): Double = this.a

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
