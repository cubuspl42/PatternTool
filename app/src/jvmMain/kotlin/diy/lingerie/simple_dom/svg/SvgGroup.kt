package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.getAttributeOrNull
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement

data class SvgGroup(
    val transformation: Transformation? = null,
    val children: List<SvgElement>,
) : SvgElement() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("g").apply {
        transformation?.let {
            val value = it.toSvgTransformationString()
            setAttribute("transform", value)
        }

        children.forEach { child ->
            appendChild(child.toRawElement(document = document))
        }
    }
}

fun SVGGElement.toSimpleGroup(): SvgGroup {
    val transform = getAttributeOrNull("transform")

    if (transform != null) {
        throw UnsupportedOperationException("Transform attribute is not supported for SVGGElement")
    }

    return SvgGroup(
        children = childElements.mapNotNull { it.toSimpleElement() },
    )
}
