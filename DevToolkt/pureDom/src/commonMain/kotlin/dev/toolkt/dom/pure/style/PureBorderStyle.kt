package dev.toolkt.dom.pure.style

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureDimension

data class PureBorderStyle(
    val width: PureDimension<*>? = null,
    val color: PureColor? = null,
    val style: Style? = null,
) : PurePropertyGroup() {
    sealed class Style : PurePropertyValue() {
        data object None : Style() {
            override val cssString: String = "none"
        }

        data object Hidden : Style() {
            override val cssString: String = "hidden"
        }

        data object Dotted : Style() {
            override val cssString: String = "dotted"
        }

        data object Dashed : Style() {
            override val cssString: String = "dashed"
        }

        data object Solid : Style() {
            override val cssString: String = "solid"
        }

        data object Double : Style() {
            override val cssString: String = "double"
        }

        data object Groove : Style() {
            override val cssString: String = "groove"
        }

        data object Ridge : Style() {
            override val cssString: String = "ridge"
        }

        data object Inset : Style() {
            override val cssString: String = "inset"
        }

        data object Outset : Style() {
            override val cssString: String = "outset"
        }
    }

    override fun applyProperties(applier: PurePropertyApplier) {
        applier.applyProperty(
            kind = PurePropertyKind.BorderWidth,
            value = width,
        )

        applier.applyProperty(
            kind = PurePropertyKind.BorderColor,
            value = color,
        )

        applier.applyProperty(
            kind = PurePropertyKind.BorderStyle,
            value = style,
        )
    }
}
