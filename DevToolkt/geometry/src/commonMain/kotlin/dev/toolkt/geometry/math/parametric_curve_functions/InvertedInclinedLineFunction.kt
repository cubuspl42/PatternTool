package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.math.avgOf
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.math.algebra.linear.vectors.Vector2

class InvertedInclinedLineFunction(
    private val s: Vector2,
    private val d: Vector2,
) : ParametricCurveFunction.InvertedCurveFunction() {
    override fun apply(a: Vector2): InversionResult {
        val point = a

        val tx = (point.x - s.x) / d.x
        val ty = (point.y - s.y) / d.y

        return InversionResult.Specific(
            t = avgOf(tx, ty),
        )
    }
}
