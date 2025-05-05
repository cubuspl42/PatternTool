package diy.lingerie.math.geometry.parametric_curve_functions

import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.divideWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.implicit_curve_functions.ImplicitLineFunction
import diy.lingerie.utils.avgOf

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
        point: Vector2,
        tolerance: NumericObject.Tolerance,
    ): Double? {
        val tx: Double? = (point.x - s.x).divideWithTolerance(
            d.x,
            tolerance = tolerance,
        )

        val ty: Double? = (point.y - s.y).divideWithTolerance(
            d.y,
            tolerance = tolerance,
        )

        return when {
            tx != null && ty != null -> {
                // If the point we locate is close tho the curve, p(avg(t_x, t_y))
                // is a point on the curve that is closer to the located point
                // than either p(t_x) or p(t_y)
                avgOf(tx, ty)
            }

            else -> tx ?: ty
        }
    }

    override fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance
    ): Double? {
        TODO("Not yet implemented")
    }
}
