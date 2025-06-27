package dev.toolkt.dom.pure.style

sealed class PureVerticalAlign(
    override val cssString: String,
) : PurePropertyValue() {

    companion object {
        fun parse(
            type: String,
        ): PureVerticalAlign = when (type.lowercase()) {
            Baseline.cssString -> Baseline
            Sub.cssString -> Sub
            Super.cssString -> Super
            TextTop.cssString -> TextTop
            TextBottom.cssString -> TextBottom
            Middle.cssString -> Middle
            Top.cssString -> Top
            Bottom.cssString -> Bottom
            else -> throw IllegalArgumentException("Unsupported vertical-align type: $type")
        }
    }

    data object Baseline : PureVerticalAlign("baseline")
    data object Sub : PureVerticalAlign("sub")
    data object Super : PureVerticalAlign("super")
    data object TextTop : PureVerticalAlign("text-top")
    data object TextBottom : PureVerticalAlign("text-bottom")
    data object Middle : PureVerticalAlign("middle")
    data object Top : PureVerticalAlign("top")
    data object Bottom : PureVerticalAlign("bottom")
}

