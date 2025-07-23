package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCurveFunction
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.InvertedCurveFunction.InversionResult
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.geometry.math.toBezierBinomial
import dev.toolkt.math.algebra.Function
import dev.toolkt.math.algebra.RealFunction
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.LowPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.findRootsNumericallyLaguerre
import dev.toolkt.math.algebra.sample

/**
 * A parametric curve function in 2D space, i.e. a function in the form f : (t: ℝ) → (p: ℝ²).
 * Given a time parameter, it returns a point in 2D space. The term "point" is
 * quire abstract in this context. In fact, it's just a 2D vector, which might be
 * interpreted as a point, as curve's velocity, or in a totally different way.
 *
 * In a corner case, this might be a constant function (associating all possible
 * t-values with a single point).
 *
 * Although the curves are defined in the real range, their 0..1 range has a
 * special meaning. In many use-cases, that range is considered the proper
 * domain of the curve.
 *
 * Specific subclasses describe specific curves with different number of control points.
 *
 * Currently, the general contract allows the objects of this class to be improperly
 * parametrized (degenerate), i.e. being geometrically equivalent to a simpler
 * parametric curve (with less control points). While such curve functions can be
 * evaluated without issues, many (nearly all?) analytical algorithms will fail
 * to work properly with them and will require some extra handling.
 */
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

    /**
     * The image of a curve (called a source curve) in the timeline of another
     * curve (called a target curve). The image is meaningful if the source curve
     * and the target curve overlap in the real range. An image can be used to
     * verify whether the source and the target curve overlap.
     */
    data class Image(
        /**
         * The image of the start parameter (0.0) mapped to the parameter of the
         * target curve
         */
        val t0: Double,
        /**
         * The image of the end parameter (1.0) mapped to the parameter of the
         * target curve.
         */
        val t1: Double,
    ) {
        val dilation: LowPolynomial.Dilation
            get() = LowPolynomial.Dilation(t1 - t0)

        val shift: LowPolynomial.Shift
            get() = LowPolynomial.Shift(t0)

        val modulation: LowPolynomial.Modulation
            get() = LowPolynomial.Modulation(
                dilation = dilation,
                shift = shift,
            )

        /**
         * Verify whether the [source] and [target] curves overlap according to
         * this image.
         */
        fun overlap(
            source: CubicBezierBinomial,
            target: CubicBezierBinomial,
            tolerance: NumericTolerance,
        ): Boolean {
            val modulatedSourcePolynomial = source.toParametricPolynomial().transform(modulation)

            val modulatedSourcePolynomialX = modulatedSourcePolynomial.xPolynomial as CubicPolynomial
            val modulatedSourcePolynomialY = modulatedSourcePolynomial.yPolynomial as CubicPolynomial

            val modulatedSource = ParametricPolynomial(
                xPolynomial = modulatedSourcePolynomialX,
                yPolynomial = modulatedSourcePolynomialY,
            ).toBezierBinomial()

            return modulatedSource.equalsWithTolerance(
                other = target,
                tolerance = tolerance,
            )
        }
    }

    /**
     * The self-intersection exists and occurs at the given t-values.
     */
    data class SelfIntersectionResult(
        val t0: Double,
        val t1: Double,
    )

    companion object {
        val primaryTRange = 0.0..1.0
    }

    fun findCriticalPoints(
        tolerance: NumericTolerance.Absolute,
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
        tolerance: NumericTolerance.Absolute,
    ): List<Double> {
        val intersectionPolynomial = findIntersectionPolynomial(other = other)

        // If this curve and the other curve are _the same curve_ (curves
        // sharing the counter-domain of points), the intersection polynomial
        // is unreliable

        return intersectionPolynomial.findTValueRoots(
            tolerance = tolerance,
        )
    }

    fun sample(n: Int): List<Sample> = this.sample(
        linSpace = LinSpace(sampleCount = n)
    ).map {
        Sample(t = it.a, point = it.b)
    }

    fun findDerivative(): ParametricPolynomial<*> = toParametricPolynomial().findDerivative()

    abstract fun findDerivativeCurve(): ParametricCurveFunction

    protected fun Polynomial.findTValueRoots(
        tolerance: NumericTolerance.Absolute,
    ): List<Double> {
        // Curve polynomials can have huge coefficients, so using the default
        // root finding strategy (which involves finding roots analytically
        // for cubic polynomials) isn't numerically stable.
        return this.findRootsNumericallyLaguerre(
            tolerance = tolerance,
        )
    }

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
        tolerance: NumericTolerance.Absolute,
    ): InvertedCurveFunction

    /**
     * @return The self-intersection result, or null which implies that the
     * curve is degenerate _or_ that the self-intersection doesn't exist.
     */
    abstract fun findSelfIntersection(
        tolerance: NumericTolerance.Absolute,
    ): SelfIntersectionResult?

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
        tolerance: NumericTolerance.Absolute,
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
        tolerance: NumericTolerance.Absolute,
    ): Double?

    /**
     * Convert this parametric curve function to an implicit curve function.
     */
    abstract fun implicitize(): ImplicitCurveFunction

    abstract fun toParametricPolynomial(): ParametricPolynomial<*>

    abstract fun toReprString(): String
}
