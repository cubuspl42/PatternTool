package diy.lingerie.geometry.transformations

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.geometry.Point

sealed class Transformation : NumericObject {

    object Identity : Transformation() {
        override fun combineWith(laterTransformations: List<SimpleTransformation>): CombinedTransformation {
            return CombinedTransformation(
                simpleTransformations = laterTransformations,
            )
        }

        override fun toSvgTransformationString(): String = ""

        override val simpleTransformations: List<PrimitiveTransformation> = emptyList()

        override val primitiveTransformations: List<PrimitiveTransformation> = emptyList()

        override fun transform(point: Point): Point = point

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    companion object {
        fun combine(
            transformations: List<Transformation>,
        ): CombinedTransformation = CombinedTransformation(
            simpleTransformations = transformations.flatMap {
                it.simpleTransformations
            },
        )
    }

    fun combineWith(
        laterTransformation: Transformation,
    ): CombinedTransformation = combineWith(
        laterTransformations = laterTransformation.simpleTransformations,
    )

    abstract fun combineWith(
        laterTransformations: List<SimpleTransformation>,
    ): CombinedTransformation

    abstract fun toSvgTransformationString(): String

    /**
     * Simple components of the transformation in the order of application.
     */
    abstract val simpleTransformations: List<SimpleTransformation>

    /**
     * Primitive components of the transformation in the order of application.
     */
    abstract val primitiveTransformations: List<PrimitiveTransformation>

    abstract fun transform(
        point: Point,
    ): Point
}

