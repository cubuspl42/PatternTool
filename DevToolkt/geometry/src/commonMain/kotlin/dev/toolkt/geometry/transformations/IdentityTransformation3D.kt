package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4

object IdentityTransformation3D : PrimitiveTransformation3D() {
    override fun transform(point: Point3D): Point3D = point

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun toMatrix(): Matrix4x4 = Matrix4x4.identity

    override fun invert(): PrimitiveTransformation3D = IdentityTransformation3D
}
