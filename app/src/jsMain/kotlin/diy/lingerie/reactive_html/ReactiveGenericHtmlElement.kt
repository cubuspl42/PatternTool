package dev.toolkt.reactive_html

import dev.toolkt.reactive.reactive_list.ReactiveList
import diy.lingerie.html.collections.ChildNodesDomList
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.EventTarget

abstract class ReactiveGenericHtmlElement() : ReactiveHtmlElement() {
    companion object {
        private fun bindChildren(
            target: Node,
            children: ReactiveList<ReactiveHtmlNode>,
        ) {
            children.map {
                it.rawNode
            }.pipe(
                target = target,
                mutableList = ChildNodesDomList(node = target),
            )
        }
    }


    override val rawElement: Element by lazy {
        document.createElement(
            localName = elementName,
        ).also { element ->
            attachEventHandlers(
                target = element,
            )

            bindChildren(
                target = element,
                children = children,
            )
        }
    }

    abstract val elementName: String

    abstract val children: ReactiveList<ReactiveHtmlNode>

    protected abstract fun attachEventHandlers(
        target: EventTarget,
    )
}