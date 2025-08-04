package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.Point3D

sealed class Transformation3D : NumericObject {

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
