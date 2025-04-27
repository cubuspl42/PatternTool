package diy.lingerie.simple_dom.svg

import diy.lingerie.utils.xml.childElements
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement

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

fun SVGGElement.toSimpleGroup(): SvgGroup = SvgGroup(
    children = childElements.map { it.toSimpleElement() },
)
