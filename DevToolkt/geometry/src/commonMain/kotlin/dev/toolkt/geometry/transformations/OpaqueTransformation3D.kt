package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4

data class OpaqueTransformation3D(
    val transformationMatrix: Matrix4x4,
) : LinearTransformation3D() {
    override fun toMatrix(): Matrix4x4 = transformationMatrix

    override fun transform(point: Point3D): Point3D {
        val targetVector = transformationMatrix.apply(
            point.pointVector.toVector4(1.0),
        )

        return Point3D(
            pointVector = targetVector.subVector3,
        )
    }

    override fun invert(): Transformation3D {
        val invertedMatrix = transformationMatrix.invert() ?: throw IllegalStateException(
            "Cannot invert an opaque transformation with a singular matrix.",
        )

        return OpaqueTransformation3D(
            transformationMatrix = invertedMatrix,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
