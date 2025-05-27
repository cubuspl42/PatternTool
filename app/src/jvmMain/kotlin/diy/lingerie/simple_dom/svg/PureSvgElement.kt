package diy.lingerie.simple_dom.svg

import dev.toolkt.geometry.transformations.Transformation
import diy.lingerie.simple_dom.PureElement
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGRectElement

abstract class PureSvgElement : PureElement() {
    companion object {
        const val SVG_NS = SVGDOMImplementation.SVG_NAMESPACE_URI
    }

    protected fun Document.createSvgElement(
        name: String,
    ): Element = createElementNS(SVG_NS, "svg:$name")
}

abstract class PureSvgGraphicsElement : PureSvgElement() {
    abstract fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape>
}

fun Element.toSvgGraphicsElements(): PureSvgGraphicsElement? = when (this) {
    is SVGPathElement -> toSimplePath()
    is SVGGElement -> toSimpleGroup()
    is SVGRectElement -> toSimpleRect()
    else -> null
}
