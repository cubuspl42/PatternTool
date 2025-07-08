package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.transformations.Transformation

abstract class PureSvgGraphicsElement : PureSvgElement() {
    abstract fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape>
}
