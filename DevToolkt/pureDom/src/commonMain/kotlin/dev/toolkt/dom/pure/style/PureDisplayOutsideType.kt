package dev.toolkt.dom.pure.style

sealed class PureDisplayOutsideType(
    override val cssString: String,
) : PureDisplayType() {
    companion object {
        fun parse(
            type: String,
        ): PureDisplayOutsideType = when (type.lowercase()) {
            Block.cssString -> Block
            Inline.cssString -> Inline
            else -> throw IllegalArgumentException("Unknown display-outside type: $type")
        }
    }

    data object Block : PureDisplayOutsideType("block")
    data object Inline : PureDisplayOutsideType("inline")
}
