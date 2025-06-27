package dev.toolkt.dom.pure.style

sealed class PureFlexAlignItems(
    override val cssString: String,
) : PurePropertyValue() {

    companion object {
        fun parse(
            type: String,
        ): PureFlexAlignItems = when (type.lowercase()) {
            Start.cssString -> Start
            End.cssString -> End
            Center.cssString -> Center
            Baseline.cssString -> Baseline
            Stretch.cssString -> Stretch
            else -> throw IllegalArgumentException("Unsupported flex-align-items type: $type")
        }
    }

    data object Start : PureFlexAlignItems("flex-start")
    data object End : PureFlexAlignItems("flex-end")
    data object Center : PureFlexAlignItems("center")
    data object Baseline : PureFlexAlignItems("baseline")
    data object Stretch : PureFlexAlignItems("stretch")
}
