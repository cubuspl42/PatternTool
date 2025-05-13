package diy.lingerie.geometry

import kotlin.math.abs

interface SpatialObject : GeometricObject {
    data class SpatialTolerance(
        val spanTolerance: Span,
    ) {
        companion object {
            val default = SpatialTolerance(
                spanTolerance = Span.of(
                    value = 1e-6,
                ),
            )
        }

        operator fun times(
            factor: Int,
        ): SpatialTolerance = SpatialTolerance(
            spanTolerance = spanTolerance * factor.toDouble(),
        )

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
