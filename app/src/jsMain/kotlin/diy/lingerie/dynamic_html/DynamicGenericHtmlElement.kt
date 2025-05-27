package diy.lingerie.dynamic_html

import diy.lingerie.reactive.dynamic_list.DynamicList
import diy.lingerie.html.collections.ChildNodesDomList
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.EventTarget

abstract class DynamicGenericHtmlElement() : DynamicHtmlElement() {
    companion object {
        private fun bindChildren(
            target: Node,
            children: DynamicList<DynamicHtmlNode>,
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

    abstract val children: DynamicList<DynamicHtmlNode>

    protected abstract fun attachEventHandlers(
        target: EventTarget,
    )
}