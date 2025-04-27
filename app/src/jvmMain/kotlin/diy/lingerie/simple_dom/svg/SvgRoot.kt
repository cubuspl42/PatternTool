package diy.lingerie.simple_dom.svg

import diy.lingerie.simple_dom.SimpleUnit
import org.w3c.dom.Document
import org.w3c.dom.Element

data class SvgRoot(
    val children: List<SvgElement>,
    val width: Int,
    val height: Int,
    val unit: SimpleUnit,
) : SvgElement() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("svg").apply {
        setAttribute("width", "$width${unit.suffix}")
        setAttribute("height", "$height${unit.suffix}")
        setAttribute("viewBox", "0 0 $width $height")

        children.forEach { child ->
            appendChild(child.toRawElement(document = document))
        }
    }
}