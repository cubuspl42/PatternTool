package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D

sealed class Transformation3D : NumericObject {
    object Identity : Transformation3D() {
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

    fun combineWith(
        laterTransformation: Transformation3D,
    ): CombinedTransformation3D = combineWith(
        laterTransformations = laterTransformation.standaloneTransformations,
    )

    abstract fun combineWith(
        laterTransformations: List<StandaloneTransformation3D>,
    ): CombinedTransformation3D

    /**
     * Simple components of the transformation in the order of application.
     */
    abstract val standaloneTransformations: List<StandaloneTransformation3D>

    abstract fun transform(
        point: Point3D,
    ): Point3D
}
