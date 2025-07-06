package dev.toolkt.dom.pure.style

sealed class PureBoxSizing(
    override val cssString: String,
) : PurePropertyValue() {
    data object BorderBox : PureBoxSizing("border-box")

    data object ContentBox : PureBoxSizing("content-box")
}
