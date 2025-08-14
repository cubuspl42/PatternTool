package dev.toolkt.dom.pure.style

import dev.toolkt.dom.pure.PureDimension

data class PureFlexStyle(
    override val outsideType: PureDisplayOutsideType? = null,
    val basis: PureDimension<*>? = null,
    val grow: Double? = null,
    val shrink: Double? = null,
    val direction: PureFlexDirection? = null,
    val alignItems: PureFlexAlignItems? = null,
    val justifyContent: PureFlexJustifyContent? = null,
    val gap: PureDimension<*>? = null,
) : PureDualDisplayStyle() {
    override val insideType: PureDisplayInsideType = PureDisplayInsideType.Flex

    override fun applySpecificDisplayProperties(
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

        applier.applyProperty(
            kind = PurePropertyKind.FlexDirection,
            value = direction,
        )

        applier.applyProperty(
            kind = PurePropertyKind.AlignItems,
            value = alignItems,
        )

        applier.applyProperty(
            kind = PurePropertyKind.JustifyContent,
            value = justifyContent,
        )

        applier.applyProperty(
            kind = PurePropertyKind.Gap,
            value = gap,
        )
    }
}
