package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance

data class CombinedTransformation3D(
    val combinedTransformations: List<PrimitiveTransformation3D>,
) : ComplexTransformation3D() {
    companion object;

    override fun invert(): CombinedTransformation3D = CombinedTransformation3D(
        combinedTransformations = combinedTransformations.map { it.invert() }.reversed(),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override val primitiveTransformations: Iterable<PrimitiveTransformation3D>
        get() = combinedTransformations
}
