package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2

data class Projection(
    val scaling: PrimitiveTransformation.Scaling,
    val translation: PrimitiveTransformation.Translation,
) : ComplexTransformation() {
    companion object {
        /**
         * Creates a projection from NDC coordinates to viewport coordinates.
         *
         * @param width Width of the viewport in pixels.
         * @param height Height of the viewport in pixels.
         */
        fun viewport(
            width: Double,
            height: Double,
        ): Transformation {
            val transformationVector = Vector2(width, height) * 0.5

            return Projection(
                scaling = PrimitiveTransformation.Scaling(
                    scaleVector = transformationVector,
                ),
                translation = PrimitiveTransformation.Translation(
                    translationVector = transformationVector,
                ),
            ).combineWith(
                laterTransformation = PrimitiveTransformation.FlipY,
            )
        }
    }

    override fun invert(): Projection {
        val invertedScaling = scaling.invert()

        return Projection(
            scaling = invertedScaling,
            translation = translation.invert().scale(invertedScaling),
        )
    }

    override val toUniversal: PrimitiveTransformation.Universal
        get() = scaling.toUniversal.copy(
            tx = translation.tx,
            ty = translation.ty,
        )

    override val primitiveTransformations: List<PrimitiveTransformation> = listOf(
        scaling,
        translation,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
