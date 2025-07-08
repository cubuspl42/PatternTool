package dev.toolkt.dom.pure.style

abstract class PurePropertyValue {
    data class Dynamic(
        override val cssString: String,
    ) : PurePropertyValue()

    data class Number(
        val value: Double,
    ) : PurePropertyValue() {
        override val cssString: String
            get() = value.toString()
    }

    abstract val cssString: String
}
