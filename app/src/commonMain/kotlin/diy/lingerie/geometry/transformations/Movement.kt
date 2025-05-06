package diy.lingerie.geometry.transformations

import diy.lingerie.math.algebra.NumericObject

data class Movement(
    val rotation: PrimitiveTransformation.Rotation,
    val translation: PrimitiveTransformation.Translation,
) : ComplexTransformation() {
    override fun invert(): Movement {
        val invertedRotation = rotation.invert()

        return Movement(
            rotation = invertedRotation,
            translation = translation.invert().rotate(invertedRotation),
        )
    }

    override val primitiveTransformations: List<PrimitiveTransformation> = listOf(
        rotation,
        translation,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
