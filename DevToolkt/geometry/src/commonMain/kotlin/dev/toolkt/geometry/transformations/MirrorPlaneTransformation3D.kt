package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Plane
import dev.toolkt.geometry.Point3D

data class MirrorPlaneTransformation3D(
    val mirrorPlane: Plane,
) : StandaloneTransformation3D() {
    override fun invert(): MirrorPlaneTransformation3D = this

    override val primitiveTransformations: List<PrimitiveTransformation3D>
        get() = TODO("Not yet implemented")

    override fun transform(point: Point3D): Point3D {
        // Find the projection vector onto the plane and apply it twice
        TODO("Not yet implemented")
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
