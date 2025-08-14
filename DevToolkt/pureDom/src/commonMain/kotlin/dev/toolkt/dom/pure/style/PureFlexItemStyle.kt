package dev.toolkt.dom.pure.style

import dev.toolkt.dom.pure.PureDimension

data class PureFlexItemStyle(
    val basis: PureDimension<*>? = null,
    val grow: Double? = null,
    val shrink: Double? = null,
) : PurePropertyGroup() {
    override fun applyProperties(
        applier: PurePropertyApplier,
    ) {
        applier.applyProperty(
            kind = PurePropertyKind.FlexBasis,
            value = basis,
        )

        applier.applyProperty(
            kind = PurePropertyKind.FlexGrow,
            value = grow?.let { PurePropertyValue.Number(it) },
        )

        applier.applyProperty(
            kind = PurePropertyKind.FlexShrink,
            value = shrink?.let { PurePropertyValue.Number(it) },
        )
    }
}
