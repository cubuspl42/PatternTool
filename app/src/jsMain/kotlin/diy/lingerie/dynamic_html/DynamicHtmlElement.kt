package diy.lingerie.dynamic_html

import org.w3c.dom.Element
import org.w3c.dom.Node

abstract class DynamicHtmlElement : DynamicHtmlNode() {
    final override val rawNode: Node
        get() = rawElement

    abstract val rawElement: Element
}
