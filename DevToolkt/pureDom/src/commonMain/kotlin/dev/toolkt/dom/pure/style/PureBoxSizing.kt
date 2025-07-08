package dev.toolkt.dom.pure.style

sealed class PureBoxSizing : PurePropertyValue() {
    data object BorderBox : PureBoxSizing() {
        override val cssString: String = "border-box"
    }

    data object ContentBox : PureBoxSizing() {
        override val cssString: String = "content-box"
    }
}
