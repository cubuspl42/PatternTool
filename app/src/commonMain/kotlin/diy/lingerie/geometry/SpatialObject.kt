package diy.lingerie.geometry

import kotlin.math.abs

interface SpatialObject : GeometricObject {
    data class SpatialTolerance(
        val spanTolerance: Span,
    ) {
        fun equalsApproximately(
            one: Span,
            another: Span,
        ): Boolean = abs(one.valueSquared - another.valueSquared) <= spanTolerance.valueSquared
    }

    override fun equalsWithGeometricTolerance(
        other: GeometricObject,
        tolerance: GeometricObject.GeometricTolerance,
    ): Boolean = when {
        other !is SpatialObject -> false

        else -> equalsWithSpatialTolerance(
            other,
            tolerance = tolerance.spatialTolerance,
        )
    }

    fun equalsWithSpatialTolerance(
        other: SpatialObject,
        tolerance: SpatialTolerance,
    ): Boolean
}
