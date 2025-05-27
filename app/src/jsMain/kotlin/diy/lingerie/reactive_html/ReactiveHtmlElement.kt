package dev.toolkt.reactive_html

import org.w3c.dom.Element
import org.w3c.dom.Node

abstract class ReactiveHtmlElement : ReactiveHtmlNode() {
    final override val rawNode: Node
        get() = rawElement

    abstract val rawElement: Element
}
