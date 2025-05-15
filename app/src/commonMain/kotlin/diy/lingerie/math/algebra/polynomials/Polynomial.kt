package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.LowPolynomial.Projection
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.untrail
import kotlin.math.max

private fun areCloseNever(
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
        ): Polynomial = HighPolynomial.normalized(
            coefficients = coefficients,
        )

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
     * Find the roots using the strategy appropriate for the polynomial's degree
     */
    fun findRoots(
        maxDepth: Int = 20,
        guessedRoot: Double = 0.5,
        tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
        areClose: (x0: Double, x1: Double) -> Boolean = ::areCloseNever,
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
        get() = normalized(
            coefficients = coefficients.mapIndexed { i, ai ->
                i * ai
            }.drop(1),
        )
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

/**
 * A polynomial of a low degree (at most cubic)
 */
sealed class LowPolynomial : Polynomial {
    data class Projection(
        val shift: Double,
        val dilation: Double,
    ) : NumericObject {
        init {
            require(dilation != 0.0) { "Dilation must not be zero" }
        }

        fun invert(): Projection = Projection(
            shift = -shift / dilation,
            dilation = 1.0 / dilation,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance
        ): Boolean = when {
            other !is Projection -> false
            !shift.equalsWithTolerance(other.shift, tolerance = tolerance) -> false
            !dilation.equalsWithTolerance(other.dilation, tolerance = tolerance) -> false
            else -> true
        }
    }

    interface OriginForm : RealFunction<Double> {
        val origin: Vector2

        val horizontalScale: Double?

        /**
         * Normalize scale-wise, but keep the origin
         *
         * @return the normalized form and the normalization scale
         */
        fun normalize(): Pair<OriginForm, Double>

        fun toStandardForm(): LowPolynomial
    }

    /**
     * Normalize, moving the polynomial to X=0
     *
     * @return the normalized polynomial and the normalization projection, or
     * null if this polynomial couldn't be normalized (is constant)
     */
    fun normalize(): Pair<LowPolynomial, Projection>? {
        val originForm = toOriginForm() ?: return null
        val (normalizedOriginForm, dilation) = originForm.normalize()

        val shift = originForm.origin.a0

        val projection = Projection(
            shift = shift,
            dilation = dilation,
        )

        return Pair(
            normalizedOriginForm.toStandardForm().shiftBy(-shift),
            projection,
        )
    }

    fun shiftBy(
        dx: Double,
    ): LowPolynomial {
        // f(x - dx)
        return substitute(
            // (x - dx)
            LinearPolynomial(
                a0 = -dx,
                a1 = 1.0,
            ),
        )
    }

    fun project(
        projection: Projection,
    ): LowPolynomial = substitute(
        LinearPolynomial(
            a0 = 0.0,
            a1 = 1.0 / projection.dilation,
        ),
    ).substitute(
        LinearPolynomial(
            a0 = -projection.shift,
            a1 = 1.0,
        ),
    )

    val prettyString: String
        get() = coefficients.mapIndexed { index, coefficient ->
            when (index) {
                0 -> coefficient.toString()
                else -> "$coefficient x^$index"
            }
        }.joinToString(separator = " + ")

    override fun findRoots(
        maxDepth: Int,
        guessedRoot: Double,
        tolerance: NumericObject.Tolerance,
        areClose: (Double, Double) -> Boolean,
    ): List<Double> = findRootsAnalytically()

    abstract val isNormalized: Boolean

    abstract fun toOriginForm(): OriginForm?

    abstract fun findRootsAnalytically(): List<Double>

    abstract fun substitute(
        p: LinearPolynomial,
    ): LowPolynomial
}

val LowPolynomial.OriginForm.normalProjection
    get(): Projection? {
        return Projection(
            shift = origin.a0,
            dilation = horizontalScale ?: return null,
        )
    }

val LowPolynomial.derivativeSubCubic: SubCubicPolynomial
    get() = this.derivative as SubCubicPolynomial

/**
 * A polynomial of a degree smaller than cubic (at most quadratic)
 */
sealed class SubCubicPolynomial : LowPolynomial() {
    final override val a3: Nothing?
        get() = null

    operator fun plus(
        other: SubCubicPolynomial,
    ): SubCubicPolynomial = QuadraticPolynomial.normalized(
        a0 = a0 + other.a0,
        a1 = (a1 ?: 0.0) + (other.a1 ?: 0.0),
        a2 = (a2 ?: 0.0) + (other.a2 ?: 0.0),
    )
}

/**
 * A polynomial of a degree smaller than quadratic (at most linear)
 */
sealed class SubQuadraticPolynomial : SubCubicPolynomial() {
    final override val a2: Nothing?
        get() = null

    operator fun plus(
        other: SubQuadraticPolynomial,
    ): SubQuadraticPolynomial = LinearPolynomial.normalized(
        a0 = a0 + other.a0,
        a1 = (a1 ?: 0.0) + (other.a1 ?: 0.0),
    )
}

/**
 * A polynomial of a degree smaller than linear (effectively constant)
 */
sealed class SubLinearPolynomial : SubQuadraticPolynomial() {
    final override val a1: Nothing?
        get() = null

    operator fun plus(
        other: SubLinearPolynomial,
    ): ConstantPolynomial = ConstantPolynomial(
        a0 + other.a0,
    )
}

/**
 * A polynomial of a degree higher than constant (at least linear)
 */
sealed interface SuperConstantPolynomial : Polynomial {
    override fun divide(
        x0: Double,
    ): Pair<Polynomial, Double> {
        if (degree == 1) {
            return Pair(ConstantPolynomial.zero, a0)
        }

        val (highestDegreeCoefficient, lowerDegreeCoefficients) = coefficients.reversed().uncons()!!

        val intermediateCoefficients = lowerDegreeCoefficients.scan(
            initial = highestDegreeCoefficient,
        ) { higherDegreeCoefficient, coefficient ->
            higherDegreeCoefficient * x0 + coefficient
        }

        val (quotientCoefficients, remainder) = intermediateCoefficients.untrail()!!

        val quotient = Polynomial.normalized(
            coefficients = quotientCoefficients.reversed(),
        )

        return Pair(quotient, remainder)
    }

    override val a1: Double
}

/**
 * A polynomial of a degree higher than linear (at least quadratic)
 */
sealed interface SuperLinearPolynomial : SuperConstantPolynomial {
    override val a2: Double
}

/**
 * A polynomial of a degree higher than quadratic (at least cubic)
 */
sealed interface SuperQuadraticPolynomial : SuperLinearPolynomial {
    override val a3: Double
}
