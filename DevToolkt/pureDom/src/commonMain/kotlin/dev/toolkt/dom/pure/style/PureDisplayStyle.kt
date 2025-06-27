package dev.toolkt.dom.pure.style

sealed class PureDisplayStyle : PurePropertyGroup() {
    final override fun applyProperties(
        applier: PurePropertyApplier,
    ) {
        applier.applyProperty(
            kind = PurePropertyKind.Display,
            value = displayType,
        )

        applySpecificDisplayProperties(
            applier = applier,
        )
    }

    abstract val outsideType: PureDisplayOutsideType?

    abstract val insideType: PureDisplayInsideType

    val displayType: PureComplexDisplayType
        get() = PureComplexDisplayType(
            outsideType = outsideType,
            insideType = insideType,
        )

    abstract fun applySpecificDisplayProperties(
        applier: PurePropertyApplier,
    )
}
