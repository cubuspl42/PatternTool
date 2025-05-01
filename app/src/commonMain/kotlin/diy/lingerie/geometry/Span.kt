package diy.lingerie.geometry

import kotlin.math.sqrt

/**
 * A measure of length or distance in 2D space.
 */
data class Span(
    val valueSquared: Double,
) : SpatialObject, Comparable<Span> {
    companion object {
        val Zero = Span(valueSquared = 0.0)
    }

    val value: Double
        get() = sqrt(valueSquared)

    override fun equalsSpatially(
        other: SpatialObject,
        tolerance: SpatialObject.SpatialTolerance,
    ): Boolean = when {
        other !is Span -> false
        else -> tolerance.equalsApproximately(this, other)
    }

    override fun compareTo(
        other: Span,
    ): Int = valueSquared.compareTo(other.valueSquared)

    operator fun div(other: Span): Double = sqrt(valueSquared / other.valueSquared)
}
