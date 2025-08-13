package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.bind
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle

fun Document.createReactiveElement(
    namespace: String? = null,
    /**
     * A name of a styleable element
     */
    name: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): Element = createElement(
    namespace = namespace,
    name = name,
).apply {
    bind(
        style = style,
        children = children,
    )
}

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

private fun Document.createElement(
    namespace: String? = null,
    name: String,
): Element = when {
    namespace != null -> this.createElementNS(
        namespace = namespace,
        qualifiedName = name,
    )

    else -> this.createElement(
        localName = name,
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
