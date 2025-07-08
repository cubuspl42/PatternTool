package dev.toolkt.dom.pure.style

sealed class PurePointerEvents : PurePropertyValue() {
    data object Auto : PurePointerEvents() {
        override val cssString: String = "auto"
    }

    data object None : PurePointerEvents() {
        override val cssString: String = "none"
    }

    // SVG-only
    data object All : PurePointerEvents() {
        override val cssString: String = "all"
    }
}
