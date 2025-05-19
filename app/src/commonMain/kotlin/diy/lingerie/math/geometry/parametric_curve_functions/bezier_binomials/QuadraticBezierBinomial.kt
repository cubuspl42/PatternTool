package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.linear.vectors.times
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.SubCubicParametricPolynomial
import diy.lingerie.math.geometry.implicit_curve_functions.ImplicitCurveFunction
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricLineFunction
import diy.lingerie.utils.iterable.LinSpace
import diy.lingerie.utils.sq
import kotlin.math.asinh
import kotlin.math.pow
import kotlin.math.sqrt

class QuadraticBezierBinomial(
    val point0: Vector2,
    val point1: Vector2,
    val point2: Vector2,
) : BezierBinomial() {
    private val delta0: Vector2
        get() = point1 - point0

    private val delta1: Vector2
        get() = point2 - point1

    override fun toParametricPolynomial(): SubCubicParametricPolynomial = ParametricPolynomial.quadratic(
        a = point0 - 2.0 * point1 + point2,
        b = 2.0 * (point1 - point0),
        c = point0,
    )

    override fun locatePoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance,
    ): Double? {
        TODO("Not yet implemented")
    }

    override fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance,
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

    override fun toReprString(): String {
        return """
            |QuadraticBezierBinomial(
            |  point0 = ${point0.toReprString()},
            |  point1 = ${point1.toReprString()},
            |  point2 = ${point2.toReprString()},
            |)
        """.trimMargin()
    }

    val primaryArcLength: Double
        get() {
            // For total length, u = 1
            val u = 1.0

            val delta1 = point1 - point0
            val delta2 = point2 - point1

            val (dx1, dy1) = delta1
            val (dx2, dy2) = delta2

            val a = (dx2 - dx1).sq + (dy2 - dy1).sq
            val b = dx1 * (dx2 - dx1) + dy1 * (dy2 - dy1)
            val c = dx1.sq + dy1.sq

            val d = u + b / a
            val e = sqrt(c + 2.0 * b * u + a * u.sq)
            val f = (a * c - b.sq) / a.pow(3.0 / 2.0)
            val g = asinh((a * u + b) / sqrt(a * c - b.sq))

            return 2.0 * (d * e + f * g)
        }

    val primaryArcLengthApproximate: Double
        get() = calculatePrimaryArcLengthBruteForce(sampleCount = 128)

    val primaryArcLengthNearlyExact: Double
        get() = calculatePrimaryArcLengthBruteForce(sampleCount = 10000)


    fun calculatePrimaryArcLengthBruteForce(sampleCount: Int): Double = LinSpace.generateSubRanges(
        range = 0.0..1.0,
        sampleCount = sampleCount,
    ).sumOf { tRange ->
        val p0 = apply(tRange.start)
        val p1 = apply(tRange.endInclusive)
        Vector2.distance(p0, p1)
    }

    fun evaluatePartially(t: Double): ParametricLineFunction {
        val subPoint0 = point0 + delta0 * t
        val subPoint1 = point1 + delta1 * t

        return ParametricLineFunction.of(
            point0 = subPoint0,
            point1 = subPoint1,
        )
    }
}
