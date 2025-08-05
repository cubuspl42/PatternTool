package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D

object PerspectiveDistortionOut3D : Transformation3D() {
    override fun transform(point: Point3D): Point3D {
        val (pointXy, z) = point.split()

        return Point3D(
            pointVector = (pointXy.pointVector * z).toVector3(z),
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = other == PerspectiveDistortionOut3D

    override fun invert(): PerspectiveDistortionIn3D = PerspectiveDistortionIn3D
}
