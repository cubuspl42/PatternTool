package diy.lingerie.geometry.transformations

import diy.lingerie.math.algebra.NumericObject

data class Projection(
    val scaling: PrimitiveTransformation.Scaling,
    val translation: PrimitiveTransformation.Translation,
) : ComplexTransformation() {
    override fun invert(): Projection {
        val invertedScaling = scaling.invert()

        return Projection(
            scaling = invertedScaling,
            translation = translation.invert().scale(invertedScaling),
        )
    }

    override val primitiveTransformations: List<PrimitiveTransformation> = listOf(
        scaling,
        translation,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
