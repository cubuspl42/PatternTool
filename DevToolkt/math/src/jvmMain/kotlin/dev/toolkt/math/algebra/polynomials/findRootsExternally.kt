package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import org.apache.commons.math3.analysis.solvers.LaguerreSolver

fun Polynomial.findRootsExternally(
    guessedRoot: Double,
    tolerance: NumericObject.Tolerance.Absolute,
): List<Double> {
    val solver = LaguerreSolver(tolerance.absoluteTolerance)

    val roots = solver.solveAllComplex(
        coefficients.toDoubleArray(),
        guessedRoot,
    )

    return roots.mapNotNull {
        when {
            tolerance.equalsApproximately(it.imaginary, 0.0) -> it.real
            else -> null
        }
    }
}
