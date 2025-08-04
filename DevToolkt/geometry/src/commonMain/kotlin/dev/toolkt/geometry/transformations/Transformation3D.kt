package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.Point3D

sealed class Transformation3D : NumericObject {
    abstract fun transform(
        point: Point3D,
    ): Point3D

    abstract fun invert(): Transformation3D
}
