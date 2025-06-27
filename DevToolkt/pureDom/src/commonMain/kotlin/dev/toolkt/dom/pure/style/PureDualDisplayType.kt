package dev.toolkt.dom.pure.style

data class PureDualDisplayType(
    val outsideType: PureDisplayOutsideType?,
    val insideType: PureDisplayInsideType,
) : PureDisplayType() {
    override val cssDisplayString: String
        get() = listOfNotNull(
            outsideType?.cssString,
            insideType.cssString,
        ).joinToString(separator = " ")
}
