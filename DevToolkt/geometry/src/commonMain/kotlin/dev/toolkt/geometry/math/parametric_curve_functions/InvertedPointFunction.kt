package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.math.algebra.linear.vectors.Vector2

data object InvertedPointFunction : ParametricCurveFunction.InvertedCurveFunction() {
    override fun apply(
        a: Vector2,
    ): InversionResult = InversionResult.Specific(
        // If a curve degenerates to a point, each value is equally good (let's prefer 0)
        t = 0.0,
    )
}
