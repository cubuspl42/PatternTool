package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance

data class HighPolynomial internal constructor(
    override val coefficients: List<Double>,
) : SuperQuadraticPolynomial {
    companion object {
        fun normalized(
            coefficients: List<Double>,
            tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
        ): Polynomial {
            require(coefficients.isNotEmpty()) {
                "HighPolynomial must have at least one coefficient, but got empty coefficients list"
            }

            return when {
                coefficients.size > 4 -> {
                    when {
                        !coefficients.last().equalsZeroWithTolerance(tolerance = tolerance) -> HighPolynomial(
                            coefficients = coefficients,
                        )

                        else -> normalized(
                            coefficients = coefficients.dropLast(1),
                        )
                    }
                }

                else -> {
                    val a0 = coefficients.getOrNull(0) ?: 0.0
                    val a1 = coefficients.getOrNull(1) ?: 0.0
                    val a2 = coefficients.getOrNull(2) ?: 0.0
                    val a3 = coefficients.getOrNull(3) ?: 0.0

                    CubicPolynomial.normalized(
                        a0 = a0,
                        a1 = a1,
                        a2 = a2,
                        a3 = a3,
                    )
                }
            }
        }
    }

    constructor(
        a0: Double,
        vararg higherCoefficients: Double,
    ) : this(
        coefficients = listOf(a0) + higherCoefficients.toList(),
    )

    init {
        require(degree >= 4) {
            "HighPolynomial must have degree >= 4, but got degree = $degree"
        }

        require(coefficients.last() != 0.0) {
            "HighPolynomial must not have leading zero coefficient, but got coefficients = $coefficients"
        }
    }

    override val a3: Double
        get() = coefficients[3]

    override val a2: Double
        get() = coefficients[2]

    override val a1: Double
        get() = coefficients[1]

    override val a0: Double
        get() = coefficients[0]

    override fun findRoots(
        maxDepth: Int,
        range: ClosedFloatingPointRange<Double>,
        tolerance: NumericObject.Tolerance.Absolute,
    ): List<Double> = findRootsNumericallyInRange(
        range = range,
        tolerance = tolerance,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is HighPolynomial -> false
        !coefficients.equalsWithTolerance(other.coefficients, tolerance = tolerance) -> false
        else -> true
    }

    override fun apply(x: Double): Double = coefficients.foldRight(
        initial = 0.0,
    ) { ai, acc ->
        ai + acc * x
    }
}
