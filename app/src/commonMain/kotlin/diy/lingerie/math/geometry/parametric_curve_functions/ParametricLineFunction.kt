package diy.lingerie.math.geometry.parametric_curve_functions

import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.implicit_curve_functions.ImplicitLineFunction

/**
 * Represents a line in 2D space in parametric form: p = s + d * t
 *
 * Given a t-value, it returns the point on the line at that t-value.
 */
data class ParametricLineFunction(
    val d: Vector2,
    val s: Vector2,
) : ParametricCurveFunction() {
    override fun apply(a: Double): Vector2 {
        val t = a
        return s + d * t
    }

    override fun toParametricPolynomial() = ParametricPolynomial.linear(
        a1 = d,
        a0 = s,
    )

    override fun implicitize(): ImplicitLineFunction = ImplicitLineFunction(
        a = d.y,
        b = -d.x,
        c = d.cross(s),
    )

    /**
     * Solve the intersection of two lines
     *
     * @return The intersection t-value for this curve
     */
    fun solveIntersection(
        other: ParametricLineFunction,
    ): Double? = solveIntersections(other).singleOrNull()

    /**
     * Solve the equation s + d * t = p for t
     */
    override fun locatePoint(
        p: Vector2,
        tolerance: NumericObject.Tolerance,
    ): Double? = when {
        d.x != 0.0 -> (p.x - s.x) / d.x
        d.y != 0.0 -> (p.y - s.y) / d.y
        else -> null
    }
}
