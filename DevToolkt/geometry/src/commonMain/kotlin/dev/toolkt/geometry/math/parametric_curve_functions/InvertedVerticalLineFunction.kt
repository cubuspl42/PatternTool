package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.geometry.y
import dev.toolkt.math.algebra.linear.vectors.Vector2

class InvertedVerticalLineFunction(
    private val sy: Double,
    private val dy: Double,
) : ParametricCurveFunction.InvertedCurveFunction() {
    override fun apply(a: Vector2): InversionResult {
        val point = a

        return InversionResult.Specific(
            t = (point.y - sy) / dy,
        )
    }
}
