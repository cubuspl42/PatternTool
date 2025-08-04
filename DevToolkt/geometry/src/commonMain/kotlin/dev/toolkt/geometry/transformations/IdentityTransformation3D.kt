package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D

object IdentityTransformation3D : Transformation3D() {
    override fun combineWith(
        laterTransformations: List<StandaloneTransformation3D>,
    ): CombinedTransformation3D = CombinedTransformation3D(
        standaloneTransformations = laterTransformations,
    )

    override val standaloneTransformations: List<PrimitiveTransformation3D> = emptyList()

    override fun transform(point: Point3D): Point3D = point

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
