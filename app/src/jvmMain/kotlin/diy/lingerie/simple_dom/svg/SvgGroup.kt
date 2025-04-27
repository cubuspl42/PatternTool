package diy.lingerie.simple_dom.svg

import org.w3c.dom.Document
import org.w3c.dom.Element

data class SvgGroup(
    val children: List<SvgElement>,
) : SvgElement() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("g").apply {
        children.forEach { child ->
            appendChild(child.toRawElement(document = document))
        }
    }
}
