package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D

data class CombinedTransformation3D(
    override val standaloneTransformations: List<StandaloneTransformation3D>,
) : EffectiveTransformation3D() {
    override fun transform(
        point: Point3D,
    ): Point3D = standaloneTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    override fun combineWith(
        laterTransformations: List<StandaloneTransformation3D>,
    ): CombinedTransformation3D = CombinedTransformation3D(
        standaloneTransformations = standaloneTransformations + laterTransformations,
    )

    companion object;

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
