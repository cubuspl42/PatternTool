package dev.toolkt.dom.pure.style

sealed class PureDisplayInsideType(
    override val cssDisplayString: String,
) : PureDisplayType() {
    companion object {
        fun parse(
            type: String,
        ): PureDisplayInsideType = when (type.lowercase()) {
            "flow" -> Flow
            "flex" -> Flex
            else -> throw UnsupportedOperationException("Unsupported display-inside type: $type")
        }
    }

    data object Flow : PureDisplayInsideType("flow")
    data object Flex : PureDisplayInsideType("flex")
}
