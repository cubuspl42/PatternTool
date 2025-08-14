package dev.toolkt.dom.pure.style

sealed class PurePosition(
    override val cssString: String,
) : PurePropertyValue() {
    data object Static : PurePosition("static")

    data object Relative : PurePosition("relative")

    data object Absolute : PurePosition("absolute")

    data object Fixed : PurePosition("fixed")

    data object Sticky : PurePosition("sticky")
}
