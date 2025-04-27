package diy.lingerie.simple_dom.svg

import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.utils.xml.svg.createSvgDocument
import diy.lingerie.utils.xml.svg.documentSvgElement
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGDocument

data class SvgRoot(
    val children: List<SvgElement>,
    val width: Int,
    val height: Int,
    val unit: SimpleUnit,
) : SvgElement() {
    fun toSvgDocument(
        svgDomImplementation: SVGDOMImplementation,
    ): SVGDocument = svgDomImplementation.createSvgDocument().apply {
        setup(
            document = this,
            root = documentSvgElement,
        )
    }

    private fun setup(
        document: Document,
        root: Element,
    ) {
        root.run {
            setAttribute("width", "$width${unit.suffix}")
            setAttribute("height", "$height${unit.suffix}")
            setAttribute("viewBox", "0 0 $width $height")

            children.forEach { child ->
                appendChild(child.toRawElement(document = document))
            }
        }
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("svg").apply {
        setup(
            document = document,
            root = this,
        )
    }
}
