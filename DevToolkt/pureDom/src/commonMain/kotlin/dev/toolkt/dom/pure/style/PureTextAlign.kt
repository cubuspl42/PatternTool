package dev.toolkt.dom.pure.style

sealed class PureTextAlign(
    override val cssString: String,
) : PurePropertyValue() {

    companion object {
        fun parse(
            type: String,
        ): PureTextAlign = when (type.lowercase()) {
            Start.cssString -> Start
            End.cssString -> End
            Left.cssString -> Left
            Right.cssString -> Right
            Center.cssString -> Center
            Justify.cssString -> Justify
            MatchParent.cssString -> MatchParent
            else -> throw IllegalArgumentException("Unsupported text-align type: $type")
        }
    }

    data object Start : PureTextAlign("start")
    data object End : PureTextAlign("end")
    data object Left : PureTextAlign("left")
    data object Right : PureTextAlign("right")
    data object Center : PureTextAlign("center")
    data object Justify : PureTextAlign("justify")
    data object MatchParent : PureTextAlign("match-parent")
}
