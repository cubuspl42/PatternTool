package dev.toolkt.geometry.math

import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCurveFunction
import dev.toolkt.math.Ratio
import dev.toolkt.math.algebra.Function
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2

data class RationalImplicitPolynomial(
    val nominatorFunction: ImplicitCurveFunction,
    val denominatorFunction: ImplicitCurveFunction,
) : Function<Vector2, Ratio>, NumericObject {
    override fun apply(v: Vector2): Ratio = Ratio(
        nominator = nominatorFunction.apply(v),
        denominator = denominatorFunction.apply(v),
    )

    fun applyOrNull(v: Vector2): Double? = apply(v).valueOrNull

    fun findGradient(): Function<Vector2, Double> {
        val nominatorGradient = nominatorFunction.findGradient()
        val denominatorGradient = denominatorFunction.findGradient()


        // ∇h(x) = ( g(x)·∇f(x) − f(x)·∇g(x) ) / ( g(x) )².

        return object : Function<Vector2, Double> {
            override fun apply(a: Vector2): Double {
                val nominatorValue = nominatorFunction.apply(a)
                val denominatorValue = denominatorFunction.apply(a)

                val nominatorGradientValue = nominatorGradient.apply(a)
                val denominatorGradientValue = denominatorGradient.apply(a)

                val p = denominatorValue * nominatorGradientValue - nominatorValue * denominatorGradientValue
                val q = denominatorValue * denominatorValue

                return p / q
            }
        }
    }


    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is RationalImplicitPolynomial -> false
        !nominatorFunction.equalsWithTolerance(other.nominatorFunction, tolerance = tolerance) -> false
        !denominatorFunction.equalsWithTolerance(other.denominatorFunction, tolerance = tolerance) -> false
        else -> true
    }
}
