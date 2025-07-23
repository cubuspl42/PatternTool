package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.math.algebra.linear.vectors.Vector2


/**
 * Represents a curve degenerated to a single point in 2D space (associating all
 * possible t-values with a single point), i.e. a constant function.
 */
data class ParametricPointFunction(
    val p: Vector2,
) : ParametricCurveFunction(), NumericObject {
    companion object {
        val Zero = ParametricPointFunction(
            p = Vector2.Zero,
        )

        fun of(
            point0: Vector2,
        ): ParametricPointFunction = ParametricPointFunction(
            point0,
        )
    }

    override fun apply(a: Double): Vector2 = p

    override fun toParametricPolynomial() = ParametricPolynomial.constant(p)

    override fun implicitize() = TODO()

    override fun findDerivativeCurve(): ParametricPointFunction = ParametricPointFunction.Zero

    override fun buildInvertedFunction(
        tolerance: NumericTolerance.Absolute,
    ): InvertedCurveFunction = InvertedPointFunction

    override fun findSelfIntersection(
        tolerance: NumericTolerance.Absolute,
    ): Nothing? = null

    override fun locatePoint(
        point: Vector2,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute,
    ): Double? = TODO()

    override fun projectPoint(
        point: Vector2,
        tolerance: NumericTolerance.Absolute,
    ): Double = 0.0

    override fun toReprString(): String {
        return """
            |ParametricPointFunction(
            |  p = ${p.toReprString()},
            |)
        """.trimMargin()
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is ParametricPointFunction -> false
        !p.equalsWithTolerance(other.p, tolerance = tolerance) -> false
        else -> true
    }
}
