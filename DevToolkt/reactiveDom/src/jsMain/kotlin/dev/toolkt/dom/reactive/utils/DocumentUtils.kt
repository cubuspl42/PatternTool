package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.bind
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle

fun <ElementT : Element> Document.createReactiveElement(
    createElement: Document.() -> ElementT,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): ElementT = createElement().apply {
    bind(
        style = style,
        children = children,
    )
}

private fun Element.bind(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
) {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") (this as ElementCSSInlineStyle)

    style?.bind(
        styleDeclaration = this.style,
    )

    children?.bind(
        target = this,
        extract = Node::childNodesList,
    )
}
