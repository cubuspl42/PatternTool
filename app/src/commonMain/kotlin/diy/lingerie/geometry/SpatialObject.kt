package diy.lingerie.geometry

import kotlin.math.abs

interface SpatialObject {
    data class SpatialTolerance(
        val spanTolerance: Span,
    ) {
        fun equalsApproximately(
            one: Span,
            another: Span,
        ): Boolean = abs(one.valueSquared - another.valueSquared) <= spanTolerance.valueSquared
    }

    fun equalsSpatially(
        other: SpatialObject,
        tolerance: SpatialTolerance,
    ): Boolean
}
