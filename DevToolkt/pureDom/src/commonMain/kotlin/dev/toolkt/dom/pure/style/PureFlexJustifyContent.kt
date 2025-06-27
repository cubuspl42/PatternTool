package dev.toolkt.dom.pure.style

sealed class PureFlexJustifyContent(
    override val cssString: String,
) : PurePropertyValue() {

    companion object {
        fun parse(
            type: String,
        ): PureFlexJustifyContent = when (type.lowercase()) {
            Start.cssString -> Start
            End.cssString -> End
            Center.cssString -> Center
            SpaceBetween.cssString -> SpaceBetween
            SpaceAround.cssString -> SpaceAround
            else -> throw IllegalArgumentException("Unsupported flex-justify-content type: $type")
        }
    }

    data object Start : PureFlexJustifyContent("flex-start")
    data object End : PureFlexJustifyContent("flex-end")
    data object Center : PureFlexJustifyContent("center")
    data object SpaceBetween : PureFlexJustifyContent("space-between")
    data object SpaceAround : PureFlexJustifyContent("space-around")
}
