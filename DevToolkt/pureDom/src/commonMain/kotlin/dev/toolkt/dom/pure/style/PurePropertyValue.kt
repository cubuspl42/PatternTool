package dev.toolkt.dom.pure.style

abstract class PurePropertyValue {
    data class Dynamic(
        override val cssString: String,
    ) : PurePropertyValue()

    abstract val cssString: String
}
