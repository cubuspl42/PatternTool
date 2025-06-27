package dev.toolkt.dom.pure.style

data class PureComplexDisplayType(
    val outsideType: PureDisplayOutsideType?,
    val insideType: PureDisplayInsideType,
) : PurePropertyValue() {
    override val cssString: String
        get() = listOfNotNull(
            outsideType?.cssString,
            insideType.cssString,
        ).joinToString(separator = " ")
}
