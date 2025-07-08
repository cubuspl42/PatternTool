package dev.toolkt.dom.pure.style

import dev.toolkt.dom.pure.PureDimension

data class PureTableDisplayStyle(
    val borderCollapse: BorderCollapse? = null,
    val borderSpacing: PureDimension<*>? = null,
) : PureDisplayStyle() {
    data object Row : PureDisplayStyle() {
        override val displayType = PureDisplayType.TableRow

        override fun applySpecificDisplayProperties(
            applier: PurePropertyApplier,
        ) {
        }
    }

    data object Cell : PureDisplayStyle() {
        override val displayType = PureDisplayType.TableCell

        override fun applySpecificDisplayProperties(
            applier: PurePropertyApplier,
        ) {
        }
    }

    sealed class BorderCollapse : PurePropertyValue() {
        data object Collapse : BorderCollapse() {
            override val cssString: String = "collapse"
        }

        data object Separate : BorderCollapse() {
            override val cssString: String = "separate"
        }
    }

    override val displayType = PureDisplayType.Table

    override fun applySpecificDisplayProperties(
        applier: PurePropertyApplier,
    ) {
        applier.applyProperty(
            kind = PurePropertyKind.BorderCollapse,
            value = borderCollapse,
        )

        applier.applyProperty(
            kind = PurePropertyKind.BorderSpacing,
            value = borderSpacing,
        )
    }
}
