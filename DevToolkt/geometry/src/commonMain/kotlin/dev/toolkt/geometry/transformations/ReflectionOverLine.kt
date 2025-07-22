package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Line
import dev.toolkt.geometry.Point
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance

data class ReflectionOverLine(
    val line: Line,
) : ComplexTransformation() {
    override val primitiveTransformations: List<PrimitiveTransformation>
        get() = TODO("Not yet implemented")

    override fun transform(point: Point): Point {
        TODO("Not yet implemented")
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun invert(): ReflectionOverLine = this
}
