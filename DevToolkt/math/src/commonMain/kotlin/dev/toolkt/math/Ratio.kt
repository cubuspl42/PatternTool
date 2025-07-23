package dev.toolkt.math

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.divideWithTolerance
import dev.toolkt.core.numeric.equalsWithTolerance

data class Ratio(
    val nominator: Double,
    val denominator: Double,
) : NumericObject {
    companion object {
        val ZeroByZero = Ratio(
            nominator = 0.0,
            denominator = 0.0,
        )

        // This is extremely elastic tolerance, but in the current use cases,
        // both nominator and denominator are typically large. Values smaller
        // than one lead to numerical explosions. This tolerance should be
        // handled on another layer.
        private val defaultDivisionTolerance = NumericTolerance.Absolute(
            absoluteTolerance = 1e-2,
        )
    }

    val valueOrNull: Double?
        get() = nominator.divideWithTolerance(denominator, tolerance = defaultDivisionTolerance)

    val value: Double
        get() = nominator / denominator

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is Ratio -> false
        !nominator.equalsWithTolerance(other.nominator, tolerance = tolerance) -> false
        !denominator.equalsWithTolerance(other.denominator, tolerance = tolerance) -> false
        else -> true
    }
}
