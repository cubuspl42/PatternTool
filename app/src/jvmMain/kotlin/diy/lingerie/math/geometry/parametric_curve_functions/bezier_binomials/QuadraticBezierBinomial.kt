package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator

private const val relativeAccuracy = 1e-4
private const val absoluteAccuracy = 1e-2
private const val minimalIterationCount = 3
private const val maximalIterationCount = 4
private const val integrationPoints = 5

private val integrator = IterativeLegendreGaussIntegrator(
    integrationPoints,
    relativeAccuracy,
    absoluteAccuracy,
    minimalIterationCount,
    maximalIterationCount,
)

private const val maxEval = 100
private const val lower = 0.0
private const val upper = 1.0

val QuadraticBezierBinomial.primaryArcLengthGauss: Double
    get() {
        val tangent = findDerivative()

        val speedFunction = UnivariateFunction { t ->
            tangent.apply(t).magnitude
        }

        return integrator.integrate(maxEval, speedFunction, lower, upper)
    }
