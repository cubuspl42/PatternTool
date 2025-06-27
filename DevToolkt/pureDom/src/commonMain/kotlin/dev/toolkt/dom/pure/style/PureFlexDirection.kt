package dev.toolkt.dom.pure.style

sealed class PureFlexDirection(
    override val cssString: String,
) : PurePropertyValue() {

    companion object {
        fun parse(
            type: String,
        ): PureFlexDirection = when (type.lowercase()) {
            Row.cssString -> Row
            Column.cssString -> Column
            else -> throw IllegalArgumentException("Unsupported flex-direction type: $type")
        }
    }

    data object Row : PureFlexDirection("row")
    data object Column : PureFlexDirection("column")
}
