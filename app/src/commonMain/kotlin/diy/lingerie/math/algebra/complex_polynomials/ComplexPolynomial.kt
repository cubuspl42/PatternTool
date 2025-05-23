package diy.lingerie.math.algebra.complex_polynomials

import diy.lingerie.math.algebra.Complex
import diy.lingerie.math.algebra.Function
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.sumOf
import diy.lingerie.math.algebra.times
import diy.lingerie.math.algebra.toComplex
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.untrail

data class ComplexPolynomial internal constructor(
    val coefficients: List<Complex>,
) : Function<Complex, Complex>, NumericObject {
    companion object {
        val zero = ComplexPolynomial(
            coefficients = listOf(Complex.Zero),
        )

        fun normalized(
            coefficients: List<Complex>,
        ): ComplexPolynomial {
            require(coefficients.isNotEmpty())

            return when {
                coefficients.size == 1 || coefficients.last() != Complex.Zero -> ComplexPolynomial(
                    coefficients = coefficients,
                )

                else -> normalized(
                    coefficients = coefficients.dropLast(1),
                )
            }
        }
    }

    constructor(
        a0: Complex,
        vararg higherCoefficients: Complex,
    ) : this(
        coefficients = listOf(a0) + higherCoefficients.toList(),
    )

    init {
        require(coefficients.isNotEmpty())
    }

    val degree: Int
        get() = coefficients.size - 1

    val a0: Complex
        get() = coefficients.first()

    fun divide(
        x0: Complex,
    ): Pair<ComplexPolynomial, Complex> {
        if (degree == 0) {
            return Pair(zero, a0)
        }

        val (highestDegreeCoefficient, lowerDegreeCoefficients) = coefficients.reversed().uncons()!!

        val intermediateCoefficients = lowerDegreeCoefficients.scan(
            initial = highestDegreeCoefficient,
        ) { higherDegreeCoefficient, coefficient ->
            higherDegreeCoefficient * x0 + coefficient
        }

        val (quotientCoefficients, remainder) = intermediateCoefficients.untrail()!!

        val quotient = normalized(
            coefficients = quotientCoefficients.reversed(),
        )

        return Pair(quotient, remainder)
    }

    /**
     * Deflates the polynomial by a linear polynomial of the form (x - x0).
     *
     * @param x0 - a root of this polynomial
     */
    fun deflate(
        x0: Complex,
    ): ComplexPolynomial {
        // The remainder is sometimes a non-zero number (is this fine?)
        val (quotient, _) = divide(x0 = x0)

        return quotient
    }

    val derivative: ComplexPolynomial
        get() {
            if (degree == 0) {
                return zero
            }

            return normalized(
                coefficients = coefficients.mapIndexed { i, ai ->
                    i.toDouble() * ai
                }.drop(1),
            )
        }

    fun findRealRoots(
        maxDepth: Int,
        guessedRoot: Double,
        tolerance: NumericObject.Tolerance.Absolute,
    ): List<Double> = this.findRoots(
        maxDepth = maxDepth,
        guessedRoot = guessedRoot.toComplex(),
        tolerance = tolerance,
    ).mapNotNull {
        it.toReal(
            tolerance = tolerance,
        )
    }

    fun findRoots(
        maxDepth: Int = 100,
        guessedRoot: Complex = Complex.Zero,
        tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
    ): List<Complex> {
        if (degree == 0) {
            return emptyList()
        }

        return this.findRootsNumerically(
            maxDepth = maxDepth,
            guessedRoot = guessedRoot,
            tolerance = tolerance,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ComplexPolynomial -> false
        !coefficients.equalsWithTolerance(other.coefficients, tolerance = tolerance) -> false
        else -> true
    }

    override fun apply(x: Complex): Complex = coefficients.withIndex().sumOf { (i, ai) ->
        ai * x.pow(i.toDouble())
    }
}
