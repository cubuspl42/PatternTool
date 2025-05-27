package dev.toolkt.reactive_html

import org.w3c.dom.Element

class ReactiveWrapperElement(
    override val rawElement: Element,
) : ReactiveHtmlElement()
