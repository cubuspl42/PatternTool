package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point

sealed class ComplexTransformation : StandaloneTransformation() {
    override fun transform(
        point: Point,
    ): Point = primitiveTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    fun transform(
        x: Double,
        y: Double,
    ): Point = transform(
        point = Point(
            x = x,
            y = y,
        ),
    )

    override val toUniversal: PrimitiveTransformation.Universal
        get() = primitiveTransformations.fold(
            initial = Identity.toUniversal,
        ) { acc, transformation ->
            acc.mixWith(
                laterTransform = transformation.toUniversal,
            )
        }

    override fun toSvgTransformationString(): String =
        standaloneTransformations.reversed().joinToString(separator = " ") { transformation ->
            transformation.toSvgTransformationString()
        }
}
