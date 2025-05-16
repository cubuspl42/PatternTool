package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.LowPolynomial.Projection
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.untrail
import kotlin.collections.sumOf
import kotlin.math.max

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
            other: NumericObject, tolerance: NumericObject.Tolerance
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

    @Suppress("unused")
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

    abstract val symmetryAxis: Double?

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
