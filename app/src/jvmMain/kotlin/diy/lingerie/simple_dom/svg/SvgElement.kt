package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.simple_dom.SimpleElement
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGRectElement

abstract class SvgElement : SimpleElement() {
    companion object {
        const val SVG_NS = SVGDOMImplementation.SVG_NAMESPACE_URI
    }

    protected fun Document.createSvgElement(
        name: String,
    ): Element = createElementNS(SVG_NS, "svg:$name")

    abstract fun flatten(
        baseTransformation: Transformation,
    ): List<SvgShape>
}

fun Element.toSimpleElement(): SvgElement? = when (this) {
    is SVGPathElement -> toSimplePath()
    is SVGGElement -> toSimpleGroup()
    is SVGRectElement -> toSimpleRect()
    else -> null
}
