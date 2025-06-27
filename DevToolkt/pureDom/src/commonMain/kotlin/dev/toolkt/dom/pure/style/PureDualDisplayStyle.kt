package dev.toolkt.dom.pure.style

sealed class PureDualDisplayStyle : PureDisplayStyle() {

    abstract val outsideType: PureDisplayOutsideType?

    abstract val insideType: PureDisplayInsideType

    override val displayType: PureDualDisplayType
        get() = PureDualDisplayType(
            outsideType = outsideType,
            insideType = insideType,
        )
}
