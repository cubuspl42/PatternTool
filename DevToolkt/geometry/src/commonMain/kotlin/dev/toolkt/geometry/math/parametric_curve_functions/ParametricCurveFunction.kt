package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCurveFunction
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.InvertedCurveFunction.InversionResult
import dev.toolkt.math.algebra.Function
import dev.toolkt.math.algebra.RealFunction
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.sample

abstract class ParametricCurveFunction : RealFunction<Vector2> {
    abstract class InvertedCurveFunction : Function<Vector2, InversionResult> {
        sealed class InversionResult {
            /**
             * The given point corresponds to a specific t-value on the curve,
             * but it's not certain that it actually lies on the curve.
             */
            data class Specific(
                val t: Double,
            ) : InversionResult()

            /**
             * The given point lies on the self-intersection (or very close to it).
             * It's certain that it lies on the curve (multiple times, one could say),
             * but there's no single unambiguous corresponding t-value (the
             * self-intersection has at least two t-value candidates).
             */
            data object SelfIntersection : InversionResult()
        }
    }

    data class Sample(
        val t: Double,
        val point: Vector2,
    )

    companion object {
        val primaryTRange = 0.0..1.0
    }

    fun findCriticalPoints(
        tolerance: NumericObject.Tolerance.Absolute,
    ): ParametricPolynomial.RootSet = findDerivative().findRoots(
        range = primaryTRange,
        tolerance = tolerance,
    )

    fun findIntersectionPolynomial(
        other: ParametricCurveFunction,
    ): Polynomial {
        val otherImplicit = other.implicitize()
        val thisParametric = this.toParametricPolynomial()
        return otherImplicit.substitute(thisParametric)
    }

    /**
     * Solve the intersection of this parametric curve with another parametric curve
     * within the given [tRange].
     * It's preferred that this curve is the simpler of two curves.
     *
     * @return A set of intersection parameter values t for this curve.
     */
    fun solveIntersectionEquation(
        other: ParametricCurveFunction,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericObject.Tolerance.Absolute,
    ): List<Double> {
        val intersectionPolynomial = findIntersectionPolynomial(other = other)

        // If this curve and the other curve are _the same curve_ (curves
        // sharing the counter-domain of points), the intersection polynomial
        // is unreliable

        return intersectionPolynomial.findTValueRoots(
            tolerance = tolerance,
            tRange = tRange,
        )
    }

    fun sample(n: Int): List<Sample> = this.sample(
        linSpace = LinSpace(sampleCount = n)
    ).map {
        Sample(t = it.a, point = it.b)
    }

    fun findDerivative(): ParametricPolynomial<*> = toParametricPolynomial().findDerivative()

    protected fun Polynomial.findTValueRoots(
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericObject.Tolerance.Absolute,
    ): List<Double> = this.findRoots(
        range = tRange,
        tolerance = tolerance,
    )

    val primaryArcLengthApproximate: Double
        get() = calculatePrimaryArcLengthBruteForce(sampleCount = 128)

    val primaryArcLengthNearlyExact: Double
        get() = calculatePrimaryArcLengthBruteForce()

    fun calculatePrimaryArcLengthBruteForce(
        range: ClosedFloatingPointRange<Double> = 0.0..1.0,
        sampleCount: Int = 8192,
    ): Double = LinSpace.generateSubRanges(
        range = range,
        sampleCount = sampleCount,
    ).sumOf { tRange ->
        val p0 = apply(tRange.start)
        val p1 = apply(tRange.endInclusive)
        Vector2.Companion.distance(p0, p1)
    }

    abstract fun buildInvertedFunction(
        tolerance: NumericObject.Tolerance.Absolute,
    ): InvertedCurveFunction

    /**
     * Locate a [point] lying on the curve.
     *
     * @return A coordinate for the [point] (if it's on the curve or reasonably close to the curve),
     * or null if no coordinate could be found within the given [tRange]. For points not lying on the curve,
     * this function returns a value that can be interpreted as a rough approximation of the point's projection.
     *
     * FIXME: This contract makes little sense, as it doesn't truly locate a point (it accepts points _not_ lying on the
     *  curve, which makes it no more useful than the raw inversion function)
     */
    abstract fun locatePoint(
        point: Vector2,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double?

    // FIXME: Nuke?
    /**
     * Project a [point] onto the curve.
     *
     * @return The t-value of the point on the curve closest to [point]. If the
     * t-value could not be found, null.
     */
    abstract fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double?

    /**
     * Convert this parametric curve function to an implicit curve function.
     */
    abstract fun implicitize(): ImplicitCurveFunction

    abstract fun toParametricPolynomial(): ParametricPolynomial<*>

    abstract fun toReprString(): String
}
