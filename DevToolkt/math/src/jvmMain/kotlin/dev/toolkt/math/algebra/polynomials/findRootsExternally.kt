package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsApproximatelyZero
import org.apache.commons.math3.analysis.solvers.LaguerreSolver

fun Polynomial.findRootsExternally(
    guessedRoot: Double,
    tolerance: NumericTolerance.Absolute,
): List<Double> {
    val solver = LaguerreSolver(tolerance.absoluteTolerance)

    val roots = solver.solveAllComplex(
        coefficients.toDoubleArray(),
        guessedRoot,
    )

    return roots.mapNotNull {
        when {
            tolerance.equalsApproximatelyZero(it.imaginary) -> it.real
            else -> null
        }
    }
}
