package dev.toolkt.dom.pure.style

data class PureFlowStyle(
    override val outsideType: PureDisplayOutsideType? = null,
) : PureDisplayStyle() {
    override val insideType: PureDisplayInsideType = PureDisplayInsideType.Flow

    override fun applySpecificDisplayProperties(
        applier: PurePropertyApplier,
    ) {
    }
}
