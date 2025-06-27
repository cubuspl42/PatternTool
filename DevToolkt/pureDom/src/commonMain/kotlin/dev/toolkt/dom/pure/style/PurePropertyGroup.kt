package dev.toolkt.dom.pure.style

interface PurePropertyApplier {
    fun applyProperty(
        kind: PurePropertyKind,
        value: PurePropertyValue?,
    )
}

abstract class PurePropertyGroup {
    abstract fun applyProperties(
        applier: PurePropertyApplier,
    )
}
