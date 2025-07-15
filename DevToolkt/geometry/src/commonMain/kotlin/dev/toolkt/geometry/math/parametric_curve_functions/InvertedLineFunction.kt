package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.math.algebra.linear.vectors.Vector2

abstract class InvertedLineFunction : ParametricCurveFunction.InvertedCurveFunction() {
    abstract override fun apply(
        a: Vector2,
    ): InversionResult.Specific
}
