package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance

data class CombinedTransformation(
    override val standaloneTransformations: List<StandaloneTransformation>,
) : EffectiveTransformation() {
    override fun transform(
        point: Point,
    ): Point = standaloneTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    override fun invert(): CombinedTransformation = CombinedTransformation(
        standaloneTransformations = standaloneTransformations.map { it.invert() }.reversed(),
    )

    override fun combineWith(
        laterTransformations: List<StandaloneTransformation>,
    ): CombinedTransformation = CombinedTransformation(
        standaloneTransformations = standaloneTransformations + laterTransformations,
    )

    companion object;

    override fun toSvgTransformationString(): String =
        standaloneTransformations.reversed().joinToString(separator = " ") { transformation ->
            transformation.toSvgTransformationString()
        }

    override val toUniversal: PrimitiveTransformation.Universal
        get() = standaloneTransformations.fold(
            initial = Identity.toUniversal,
        ) { acc, transformation ->
            acc.mixWith(
                laterTransform = transformation.toUniversal,
            )
        }

    override val primitiveTransformations: List<PrimitiveTransformation>
        get() = standaloneTransformations.flatMap { it.primitiveTransformations }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
