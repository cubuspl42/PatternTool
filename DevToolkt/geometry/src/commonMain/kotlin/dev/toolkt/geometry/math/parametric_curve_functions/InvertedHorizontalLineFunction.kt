package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.geometry.x
import dev.toolkt.math.algebra.linear.vectors.Vector2

class InvertedHorizontalLineFunction(
    private val sx: Double,
    private val dx: Double,
) : ParametricCurveFunction.InvertedCurveFunction() {
    override fun apply(a: Vector2): InversionResult {
        val point = a

        return InversionResult.Specific(
            t = (point.x - sx) / dx,
        )
    }
}
