package dev.toolkt.dom.pure.style

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureDimension

data class PureStrokeStyle(
    val color: PureColor? = null,
    val width: PureDimension<*>? = null,
) : PurePropertyGroup() {

    override fun applyProperties(applier: PurePropertyApplier) {
        applier.applyProperty(
            kind = PurePropertyKind.Stroke,
            value = color,
        )

        applier.applyProperty(
            kind = PurePropertyKind.StrokeWidth,
            value = width,
        )
    }
}
