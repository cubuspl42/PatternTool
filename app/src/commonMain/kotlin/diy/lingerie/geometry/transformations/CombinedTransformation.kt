package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Point
import diy.lingerie.math.algebra.NumericObject

data class CombinedTransformation(
    override val simpleTransformations: List<SimpleTransformation>,
) : Transformation() {
    override fun transform(
        point: Point,
    ): Point = simpleTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    override fun combineWith(
        laterTransformations: List<SimpleTransformation>,
    ): CombinedTransformation = CombinedTransformation(
        simpleTransformations = simpleTransformations + laterTransformations,
    )

    companion object;

    override fun toSvgTransformationString(): String =
        simpleTransformations.reversed().joinToString(separator = " ") { transformation ->
            transformation.toSvgTransformationString()
        }

    override val primitiveTransformations: List<PrimitiveTransformation>
        get() = simpleTransformations.flatMap { it.primitiveTransformations }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
