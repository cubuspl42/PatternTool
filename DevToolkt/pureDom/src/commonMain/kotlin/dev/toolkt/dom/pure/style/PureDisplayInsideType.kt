package dev.toolkt.dom.pure.style

sealed class PureDisplayInsideType(
    override val cssDisplayString: String,
) : PureDisplayType() {
    data object Flow : PureDisplayInsideType("flow")

    data object Flex : PureDisplayInsideType("flex")

    data object Block : PureDisplayInsideType("block")
}
