package dev.toolkt.dom.pure.style

data class PureBlockStyle(
    override val outsideType: PureDisplayOutsideType? = null,
) : PureDualDisplayStyle() {
    override val insideType: PureDisplayInsideType = PureDisplayInsideType.Block

    override fun applySpecificDisplayProperties(
        applier: PurePropertyApplier,
    ) {
    }
}
