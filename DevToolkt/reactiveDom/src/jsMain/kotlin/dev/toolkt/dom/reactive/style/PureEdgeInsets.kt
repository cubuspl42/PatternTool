package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.style.PurePropertyApplier
import dev.toolkt.dom.pure.style.PurePropertyKind
import dev.toolkt.dom.pure.style.PurePropertyValue
import dev.toolkt.dom.pure.style.setOrRemoveProperty
import org.w3c.dom.css.CSSStyleDeclaration

data class PureEdgeInsets(
    val left: PureDimension<*>,
    val top: PureDimension<*>,
    val right: PureDimension<*>,
    val bottom: PureDimension<*>,
) {
    sealed class InsetKind() {
        data object Margin : InsetKind() {
            override val leftPropertyKind: PurePropertyKind = PurePropertyKind.MarginLeft

            override val topPropertyKind: PurePropertyKind = PurePropertyKind.MarginTop

            override val rightPropertyKind: PurePropertyKind = PurePropertyKind.MarginRight

            override val bottomPropertyKind: PurePropertyKind = PurePropertyKind.MarginBottom
        }

        data object Padding : InsetKind() {
            override val leftPropertyKind: PurePropertyKind = PurePropertyKind.PaddingLeft

            override val topPropertyKind: PurePropertyKind = PurePropertyKind.PaddingTop

            override val rightPropertyKind: PurePropertyKind = PurePropertyKind.PaddingRight

            override val bottomPropertyKind: PurePropertyKind = PurePropertyKind.PaddingBottom
        }

        abstract val leftPropertyKind: PurePropertyKind

        abstract val topPropertyKind: PurePropertyKind

        abstract val rightPropertyKind: PurePropertyKind

        abstract val bottomPropertyKind: PurePropertyKind

        fun clearProperties(
            applier: PurePropertyApplier,
        ) {
            applier.applyProperty(
                kind = leftPropertyKind,
                value = null,
            )

            applier.applyProperty(
                kind = topPropertyKind,
                value = null,
            )

            applier.applyProperty(
                kind = rightPropertyKind,
                value = null,
            )

            applier.applyProperty(
                kind = bottomPropertyKind,
                value = null,
            )
        }
    }

    companion object {
        fun all(
            value: PureDimension<*>,
        ): PureEdgeInsets = PureEdgeInsets(
            left = value,
            top = value,
            right = value,
            bottom = value,
        )
    }

    fun applyProperties(
        insetKind: InsetKind,
        applier: PurePropertyApplier,
    ) {
        applier.applyProperty(
            kind = insetKind.leftPropertyKind,
            value = left,
        )

        applier.applyProperty(
            kind = insetKind.topPropertyKind,
            value = top,
        )

        applier.applyProperty(
            kind = insetKind.rightPropertyKind,
            value = right,
        )

        applier.applyProperty(
            kind = insetKind.bottomPropertyKind,
            value = bottom,
        )
    }
}
