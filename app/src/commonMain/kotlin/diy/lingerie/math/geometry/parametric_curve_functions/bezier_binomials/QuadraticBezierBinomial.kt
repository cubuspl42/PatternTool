package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.linear.vectors.times
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.implicit_curve_functions.ImplicitCurveFunction

class QuadraticBezierBinomial(
    val point0: Vector2,
    val point1: Vector2,
    val point2: Vector2,
) : BezierBinomial() {
    override fun toParametricPolynomial() = ParametricPolynomial.quadratic(
        a = point0 - 2.0 * point1 + point2,
        b = 2.0 * (point1 - point0),
        c = point0,
    )

    override fun locatePoint(
        p: Vector2,
        tolerance: NumericObject.Tolerance
    ): Double? {
        TODO("Not yet implemented")
    }

    override fun implicitize(): ImplicitCurveFunction {
        TODO("Not yet implemented")
    }

    override fun apply(a: Double): Vector2 {
        val t = a

        val u = 1.0 - t
        val c1 = u * u * point0
        val c2 = 2.0 * u * t * point1
        val c3 = t * t * point2
        return c1 + c2 + c3
    }
}
