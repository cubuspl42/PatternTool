package dev.toolkt.geometry.transformations

import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4

sealed class LinearTransformation3D : Transformation3D() {
    companion object {
        fun toMatrix(
            transformations: Iterable<LinearTransformation3D>,
        ): Matrix4x4 = transformations.fold(Matrix4x4.identity) { acc, transformation ->
            acc * transformation.toMatrix()
        }
    }

    abstract fun toMatrix(): Matrix4x4
}
