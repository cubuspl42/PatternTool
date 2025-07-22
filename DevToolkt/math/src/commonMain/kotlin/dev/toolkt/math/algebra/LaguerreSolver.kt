package dev.toolkt.math.algebra

import dev.toolkt.core.iterable.untrail
import dev.toolkt.core.math.Complex
import dev.toolkt.core.math.div
import dev.toolkt.core.math.maxBy
import dev.toolkt.core.math.sq
import dev.toolkt.core.math.times
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsApproximately
import dev.toolkt.core.numeric.equalsApproximatelyZero

object LaguerreSolver {
    private const val defaultMaxIterationCount: Int = 1000

    private val defaultFunctionValueTolerance: NumericTolerance.Absolute
        get() = NumericTolerance.Absolute(
            absoluteTolerance = 1e-15,
        )

    // Epsilon for perturbance in Laguerre's method.
    private val perturbanceEps: Double
        get() = NumericTolerance.Absolute.Default.absoluteTolerance

    data class ComboEvalResult(
        val pv: Complex,
        val dv: Complex,
        val d2v: Complex,
    ) {
        companion object {
            fun comboEval(
                coefficients: Array<Complex>,
                z: Complex,
            ): ComboEvalResult {
                val n = coefficients.size - 1

                var pv = coefficients[n]
                var dv = Complex.ZERO
                var d2v = Complex.ZERO

                for (j in n - 1 downTo 0) {
                    d2v = dv + z * d2v
                    dv = pv + z * dv
                    pv = coefficients[j] + z * pv
                }

                return ComboEvalResult(
                    pv = pv,
                    dv = dv,
                    d2v = d2v * 2.0,
                )
            }
        }
    }

    /**
     * Finds a single root of a polynomial with complex coefficients using Laguerre's method.
     *
     * @param convergenceTolerance
     *
     * @return the root of the polynomial, or null if the maximum number of iterations is reached without convergence.
     */
    fun solveSingle(
        coefficients: List<Complex>,
        guess: Complex,
        maxIterationCount: Int,
        convergenceTolerance: NumericTolerance,
        functionValueTolerance: NumericTolerance.Absolute,
    ): Complex? {
        require(coefficients.isNotEmpty()) { "coefficients is empty" }

        val n = coefficients.size - 1

        require(n != 0) { "n == 0" }

        tailrec fun solveRecursively(
            z: Complex,
            oldZ: Complex?,
            iterationCount: Int,
        ): Complex? {
            if (iterationCount >= maxIterationCount) {
                return null
            }

            if (oldZ != null && convergenceTolerance.equalsApproximately(value = z, reference = oldZ)) {
                return z
            }

            val evalResult = ComboEvalResult.comboEval(
                coefficients = coefficients.toTypedArray(),
                z = z,
            )

            val pv = evalResult.pv

            if (functionValueTolerance.equalsApproximatelyZero(pv)) {
                return z
            }

            val dv = evalResult.dv
            val d2v = evalResult.d2v

            val g = dv / pv
            val gSq = g.sq()
            val h = gSq - (d2v / pv)
            val delta = (n - 1) * (n * h - gSq)
            val deltaSqrt = delta.sqrt()

            val denominator = maxBy(
                a = g - deltaSqrt,
                b = g + deltaSqrt,
                selector = Complex::abs,
            )

            return when (denominator) {
                Complex.ZERO -> solveRecursively(
                    oldZ = null,
                    z = z + Complex.diagonal(perturbanceEps),
                    iterationCount = iterationCount + 1,
                )

                else -> solveRecursively(
                    oldZ = z,
                    z = z - (n / denominator),
                    iterationCount = iterationCount + 1,
                )
            }
        }

        return solveRecursively(
            z = guess,
            oldZ = null,
            iterationCount = 0,
        )
    }

    /**
     * Finds all roots of a polynomial with complex coefficients using Laguerre's method.
     *
     * @return a list of roots of the polynomial, or null if any root could not be found within the maximum number of iterations.
     */
    fun solveAll(
        coefficients: List<Complex>,
        initialGuess: Complex,
        maxIterationCount: Int = defaultMaxIterationCount,
        convergenceTolerance: NumericTolerance = NumericTolerance.Hybrid.Default,
        functionValueTolerance: NumericTolerance.Absolute = defaultFunctionValueTolerance,
    ): List<Complex>? = solveAllRecursively(
        coefficients = coefficients,
        initialGuess = initialGuess,
        maxIterationCount = maxIterationCount,
        convergenceTolerance = convergenceTolerance,
        functionValueTolerance = functionValueTolerance,
    )

    private fun solveAllRecursively(
        coefficients: List<Complex>,
        initialGuess: Complex,
        maxIterationCount: Int = defaultMaxIterationCount,
        convergenceTolerance: NumericTolerance,
        functionValueTolerance: NumericTolerance.Absolute,
    ): List<Complex>? {
        if (coefficients.size < 2) {
            return emptyList()
        }

        val singleRoot = solveSingle(
            coefficients = coefficients,
            guess = initialGuess,
            maxIterationCount = maxIterationCount,
            convergenceTolerance = convergenceTolerance,
            functionValueTolerance = functionValueTolerance,
        ) ?: return null

        val remainingRoots = solveAllRecursively(
            coefficients = deflate(
                coefficients = coefficients,
                root = singleRoot,
            ),
            initialGuess = initialGuess,
            maxIterationCount = maxIterationCount,
            convergenceTolerance = convergenceTolerance,
            functionValueTolerance = functionValueTolerance,
        ) ?: return null

        return listOf(singleRoot) + remainingRoots
    }

    fun deflate(
        coefficients: List<Complex>,
        root: Complex,
    ): List<Complex> {
        val (lowerCoefficients, highestCoefficient) = coefficients.untrail()
            ?: throw IllegalArgumentException("Cannot deflate polynomial with no coefficients")

        fun deflateRecursively(
            remainingCoefficients: List<Complex>,
            carry: Complex,
        ): List<Complex> {
            val (remainingLowerCoefficients, highestRemainingCoefficient) = remainingCoefficients.untrail()
                ?: return emptyList()

            return deflateRecursively(
                remainingCoefficients = remainingLowerCoefficients,
                carry = highestRemainingCoefficient + carry * root,
            ) + listOf(carry)
        }

        return deflateRecursively(
            remainingCoefficients = lowerCoefficients,
            carry = highestCoefficient,
        )
    }

    fun solveAll(
        coefficients: List<Double>,
        initialGuess: Double,
        maxIterationCount: Int = defaultMaxIterationCount,
        convergenceTolerance: NumericTolerance = NumericTolerance.Hybrid.Default,
        functionValueTolerance: NumericTolerance.Absolute = defaultFunctionValueTolerance,
    ): List<Complex>? = solveAll(
        coefficients = coefficients.map { Complex.ofReal(it) },
        initialGuess = Complex.ofReal(initialGuess),
        maxIterationCount = maxIterationCount,
        convergenceTolerance = convergenceTolerance,
        functionValueTolerance = functionValueTolerance,
    )
}
