package dev.toolkt.dom.pure.style

sealed class PureDisplayType() : PurePropertyValue() {
    data object Table : PureDisplayType() {
        override val cssDisplayString: String = "table"
    }

    data object TableRow : PureDisplayType() {
        override val cssDisplayString: String = "table-row"
    }

    data object TableCell : PureDisplayType() {
        override val cssDisplayString: String = "table-cell"
    }

    final override val cssString: String
        get() = cssDisplayString

    abstract val cssDisplayString: String
}
