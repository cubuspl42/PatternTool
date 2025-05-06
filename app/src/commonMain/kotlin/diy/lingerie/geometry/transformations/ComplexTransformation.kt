package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Point

sealed class ComplexTransformation : Transformation() {
    final override fun transform(
        point: Point,
    ): Point = simpleTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }
}
