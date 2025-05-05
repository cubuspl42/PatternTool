package diy.lingerie.math.geometry.parametric_curve_functions

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.sample
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.implicit_curve_functions.ImplicitCurveFunction
import diy.lingerie.utils.iterable.LinSpace

abstract class ParametricCurveFunction : RealFunction<Vector2> {
    data class Sample(
        val t: Double,
        val point: Vector2,
    )

    fun findCriticalPoints(): ParametricPolynomial.RootSet = findDerivative().findRoots()

    fun findRoots(): ParametricPolynomial.RootSet = toParametricPolynomial().findRoots()

    /**
     * Solve the intersection of this parametric curve with another parametric curve.
     *
     * @return A set of intersection parameter values t for this curve.
     */
    fun solveIntersections(
        other: ParametricCurveFunction,
    ): List<Double> {
        val otherImplicit = other.implicitize()
        val thisParametric = this.toParametricPolynomial()
        val intersectionPolynomial = otherImplicit.substitute(thisParametric)

        return intersectionPolynomial.findRoots(
            areClose = { t0, t1 ->
                val p0 = thisParametric.apply(t0)
                val p1 = thisParametric.apply(t1)

                (p0 - p1).magnitude.equalsWithTolerance(0.0)
            },
        )
    }

    fun sample(n: Int): List<Sample> = this.sample(
        linSpace = LinSpace(n = n)
    ).map {
        Sample(t = it.a, point = it.b)
    }
    
    /**
     * Locate [point] lying on the curve.
     *
     * @return parameter t for the point on the curve, or null if the point
     * could not be found (typically because the point is not on the curve)
     */
    abstract fun locatePoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance,
    ): Double?

    abstract fun implicitize(): ImplicitCurveFunction

    fun findDerivative(): ParametricPolynomial = toParametricPolynomial().findDerivative()

    abstract fun toParametricPolynomial(): ParametricPolynomial
}
