package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Rectangle

data class TransProjection(
    val sourceRectangle: Rectangle,
    val targetRectangle: Rectangle,
) : ComplexTransformation() {
    override fun invert(): TransProjection = TransProjection(
        sourceRectangle = targetRectangle,
        targetRectangle = sourceRectangle,
    )

    override val primitiveTransformations: List<PrimitiveTransformation> = listOf(
        sourceRectangle.origin.translationTo(Point.Companion.origin),
        sourceRectangle.scalingTo(targetRectangle),
        Point.Companion.origin.translationTo(targetRectangle.origin),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
