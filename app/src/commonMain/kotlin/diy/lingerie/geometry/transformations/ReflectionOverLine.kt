package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point
import diy.lingerie.math.algebra.NumericObject

data class ReflectionOverLine(
    val line: Line,
) : ComplexTransformation() {
    override fun toSvgTransformationString(): String {
        TODO("Not yet implemented")
    }

    override val primitiveTransformations: List<PrimitiveTransformation>
        get() = TODO("Not yet implemented")

    override fun transform(point: Point): Point {
        TODO("Not yet implemented")
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun invert(): ReflectionOverLine = this
}
