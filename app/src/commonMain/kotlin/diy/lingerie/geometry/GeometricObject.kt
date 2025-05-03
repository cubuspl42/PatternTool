package diy.lingerie.geometry

interface GeometricObject {
    data class GeometricTolerance(
        val spatialTolerance: SpatialObject.SpatialTolerance,
        val radialTolerance: RelativeAngle.RadialTolerance,
    )

    fun equalsWithGeometricTolerance(
        other: GeometricObject,
        tolerance: GeometricTolerance,
    ): Boolean
}
