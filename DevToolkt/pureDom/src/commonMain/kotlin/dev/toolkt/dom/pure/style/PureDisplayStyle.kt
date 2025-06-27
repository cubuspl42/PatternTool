package dev.toolkt.dom.pure.style

sealed class PureDisplayStyle : PurePropertyGroup() {
    final override fun applyProperties(applier: PurePropertyApplier) {

        applier.applyProperty(
            kind = PurePropertyKind.FlexDirection,
            value = outsideType,
        )

    }

    abstract val outsideType: PureDisplayOutsideType?

    abstract val insideType: PureDisplayInsideType

    val displayString: String
        get() = listOfNotNull(
            outsideType?.cssString,
            insideType.type,
        ).joinToString(separator = " ")

    abstract fun applySpecificDisplayProperties(
        applier: PurePropertyApplier,
    )
}
