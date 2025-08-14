package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PurePropertyApplier
import dev.toolkt.dom.pure.style.PurePropertyGroup
import dev.toolkt.dom.pure.style.PurePropertyKind

data class PureInset(
    val top: PureDimension<*>,
    val right: PureDimension<*>,
    val bottom: PureDimension<*>,
    val left: PureDimension<*>,
) : PurePropertyGroup() {
    companion object {
        val Zero = PureInset(
            top = 0.px,
            right = 0.px,
            bottom = 0.px,
            left = 0.px,
        )
    }

    override fun applyProperties(applier: PurePropertyApplier) {
        applier.applyProperty(
            kind = PurePropertyKind.Top,
            value = top,
        )

        applier.applyProperty(
            kind = PurePropertyKind.Right,
            value = right,
        )

        applier.applyProperty(
            kind = PurePropertyKind.Bottom,
            value = bottom,
        )

        applier.applyProperty(
            kind = PurePropertyKind.Left,
            value = left,
        )
    }
}
