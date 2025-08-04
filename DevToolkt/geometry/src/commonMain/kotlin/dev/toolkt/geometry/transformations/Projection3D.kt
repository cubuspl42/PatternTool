package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D

object Projection3D : StandaloneTransformation3D() {
    override fun transform(point: Point3D): Point3D {
        val (pointXy, z) = point.split()

        return Point3D(
            pointVector = (pointXy.pointVector / z).toVector3(z),
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun invert(): Unprojection3D = Unprojection3D
}
