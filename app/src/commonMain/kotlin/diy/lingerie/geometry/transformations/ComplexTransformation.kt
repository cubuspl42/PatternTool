package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Point

sealed class ComplexTransformation : StandaloneTransformation() {
    override fun transform(
        point: Point,
    ): Point = primitiveTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    override val toUniversal: PrimitiveTransformation.Universal
        get() = primitiveTransformations.fold(
            initial = Identity.toUniversal,
        ) { acc, transformation ->
            acc.mixWith(
                laterTranslation = transformation.toUniversal,
            )
        }

    override fun toSvgTransformationString(): String =
        standaloneTransformations.reversed().joinToString(separator = " ") { transformation ->
            transformation.toSvgTransformationString()
        }
}

