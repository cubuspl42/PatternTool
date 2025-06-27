package dev.toolkt.dom.pure.style

sealed class PureDisplayOutsideType(
    override val cssDisplayString: String,
) : PureDisplayType() {
    companion object {
        fun parse(
            type: String,
        ): PureDisplayOutsideType = when (type.lowercase()) {
            Block.cssDisplayString -> Block
            Inline.cssDisplayString -> Inline
            else -> throw IllegalArgumentException("Unknown display-outside type: $type")
        }
    }

    data object Block : PureDisplayOutsideType("block")
    data object Inline : PureDisplayOutsideType("inline")
}
