package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point3D
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4

sealed class ComplexTransformation3D : DescriptiveTransformation3D() {
    // Not final, as specific complex transformations may provide a more optimal implementation
    override fun transform(
        point: Point3D,
    ): Point3D = primitiveTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    final override fun toMatrix(): Matrix4x4 = LinearTransformation3D.toMatrix(
        transformations = primitiveTransformations,
    )

    abstract val primitiveTransformations: Iterable<PrimitiveTransformation3D>
}
