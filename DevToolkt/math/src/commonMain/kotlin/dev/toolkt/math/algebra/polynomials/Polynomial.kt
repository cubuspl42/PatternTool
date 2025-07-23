package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance
import dev.toolkt.math.algebra.LaguerreSolver
import dev.toolkt.math.algebra.RealFunction
import kotlin.math.max

internal fun areCloseNever(
    x0: Double,
    x1: Double,
): Boolean = false

sealed interface Polynomial : NumericObject, RealFunction<Double> {
    companion object {
        /**
         * @return a constant polynomial
         */
        fun constant(
            a0: Double,
        ): ConstantPolynomial = ConstantPolynomial(
            a0 = a0,
        )

        /**
         * @return an at-most linear normalized polynomial
         */
        fun linear(
            a0: Double,
            a1: Double,
        ): SubQuadraticPolynomial = LinearPolynomial.normalized(
            a0 = a0,
            a1 = a1,
        )

        /**
         * @return an at-most quadratic normalized polynomial
         */
        fun quadratic(
            a0: Double,
            a1: Double,
            a2: Double,
        ): SubCubicPolynomial = QuadraticPolynomial.normalized(
            a0 = a0,
            a1 = a1,
            a2 = a2,
        )

        /**
         * @return an at-most cubic normalized polynomial
         */
        fun cubic(
            a0: Double,
            a1: Double,
            a2: Double,
            a3: Double,
        ): LowPolynomial = CubicPolynomial.normalized(
            a0 = a0,
            a1 = a1,
            a2 = a2,
            a3 = a3,
        )

        /**
         * @return a normalized polynomial (of a potentially high degree)
         */
        fun normalized(
            coefficients: List<Double>,
        ): Polynomial {
            require(coefficients.isNotEmpty()) {
                "A polynomial must have at least one coefficient, but got empty coefficients list"
            }

            return HighPolynomial.normalized(
                coefficients = coefficients,
            )
        }

        /**
         * @return a normalized polynomial (of a potentially high degree)
         */
        fun normalized(
            vararg coefficients: Double,
        ): Polynomial = HighPolynomial.normalized(
            coefficients = coefficients.toList(),
        )
    }

    /**
     * A list of efficient coefficients, the highest one is non-zero
     */
    val coefficients: List<Double>

    val a3: Double?

    val a2: Double?

    val a1: Double?

    val a0: Double

    /**
     * Find the roots in the given [range] using the strategy appropriate for the polynomial's degree
     */
    fun findRoots(
        maxDepth: Int = 20,
        range: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute = NumericTolerance.Absolute.Default,
    ): List<Double>

    /**
     * Divides the polynomial by a linear polynomial of the form (x - x0).
     *
     * @return Pair of quotient and remainder.
     */
    fun divide(
        x0: Double,
    ): Pair<Polynomial, Double>

    /**
     * Deflates the polynomial by a linear polynomial of the form (x - x0).
     *
     * @param x0 - a root of this polynomial
     */
    fun deflate(
        x0: Double,
    ): Polynomial {
        // The remainder is sometimes a non-zero number (is this fine?)
        val (quotient, _) = divide(x0 = x0)

        return quotient
    }

    val derivative: Polynomial
        get() {
            require(coefficients.size > 1) {
                "Cannot compute derivative of a polynomial with degree < 1, but got degree = ${coefficients.size - 1}"
            }

            return normalized(
                coefficients = coefficients.mapIndexed { i, ai ->
                    i * ai
                }.drop(1),
            )
        }
}

val Polynomial.degree: Int
    get() = coefficients.size - 1

fun Polynomial.getCoefficient(i: Int): Double? = coefficients.getOrNull(i)

fun Polynomial.getCoefficientForced(i: Int): Double = getCoefficient(i) ?: 0.0

operator fun Polynomial.plus(
    other: Polynomial,
): Polynomial = Polynomial.normalized(
    coefficients = List(max(degree, other.degree) + 1) { i ->
        getCoefficientForced(i) + other.getCoefficientForced(i)
    },
)

operator fun Polynomial.plus(
    constant: Double,
): Polynomial = this + ConstantPolynomial(constant)

operator fun Double.plus(
    polynomial: Polynomial,
): Polynomial = ConstantPolynomial(this) + polynomial

operator fun Polynomial.times(
    other: Polynomial,
): Polynomial {
    // Degree of p0[n] * p1[m] = m + n
    val productDegree = this.degree + other.degree

    return Polynomial.normalized(
        coefficients = (0..productDegree).map { k ->
            (0..k).sumOf { i ->
                this.getCoefficientForced(i) * other.getCoefficientForced(k - i)
            }
        },
    )
}

operator fun Polynomial.times(
    scalar: Double,
): Polynomial = Polynomial.normalized(
    coefficients = coefficients.map { it * scalar },
)

operator fun Double.times(
    polynomial: Polynomial,
): Polynomial = polynomial * this

fun Polynomial.findRootsNumericallyLaguerre(
    tolerance: NumericTolerance.Absolute,
): List<Double> {
    val complexRoots = LaguerreSolver.solveAll(
        coefficients = coefficients,
        initialGuess = 0.5,
    ) ?: return emptyList()

    return complexRoots.mapNotNull { complex ->
        complex.real.takeIf {
            complex.imaginary.equalsZeroWithTolerance(tolerance = tolerance)
        }
    }
}
