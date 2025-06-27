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

    abstract val displayType: PureDisplayType

    abstract fun applySpecificDisplayProperties(
        applier: PurePropertyApplier,
    )
}
