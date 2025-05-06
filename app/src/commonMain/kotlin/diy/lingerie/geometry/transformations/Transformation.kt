package diy.lingerie.geometry.transformations

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.geometry.Point

sealed class Transformation : NumericObject {
    object Identity : Transformation() {
        override fun combineWith(laterTransformations: List<StandaloneTransformation>): CombinedTransformation {
            return CombinedTransformation(
                standaloneTransformations = laterTransformations,
            )
        }

        override fun toSvgTransformationString(): String = ""

        override val standaloneTransformations: List<PrimitiveTransformation> = emptyList()

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
            standaloneTransformations = transformations.flatMap {
                it.standaloneTransformations
            },
        )
    }

    fun combineWith(
        laterTransformation: Transformation,
    ): CombinedTransformation = combineWith(
        laterTransformations = laterTransformation.standaloneTransformations,
    )

    abstract fun combineWith(
        laterTransformations: List<StandaloneTransformation>,
    ): CombinedTransformation

    abstract fun toSvgTransformationString(): String

    /**
     * Simple components of the transformation in the order of application.
     */
    abstract val standaloneTransformations: List<StandaloneTransformation>

    /**
     * Primitive components of the transformation in the order of application.
     */
    abstract val primitiveTransformations: List<PrimitiveTransformation>

    abstract fun transform(
        point: Point,
    ): Point
}

