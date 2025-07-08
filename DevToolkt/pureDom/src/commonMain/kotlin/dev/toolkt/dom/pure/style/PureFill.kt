package dev.toolkt.dom.pure.style

import dev.toolkt.dom.pure.PureColor

sealed class PureFill() : PurePropertyValue() {
    data object None : PureFill() {
        override val cssString: String = "none"
    }

    data class Colored(
        val color: PureColor,
    ) : PureFill() {
        override val cssString: String
            get() = color.cssString
    }
}
