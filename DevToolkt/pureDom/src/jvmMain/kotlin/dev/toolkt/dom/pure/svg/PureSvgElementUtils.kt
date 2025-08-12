package dev.toolkt.dom.pure.svg

import org.w3c.dom.Element
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGRectElement

fun Element.toSvgGraphicsElements(): PureSvgGraphicsElement? = when (this) {
    is SVGPathElement -> toPurePath()
    is SVGGElement -> toPureGroup()
    is SVGRectElement -> toPureRect()
    is SVGLineElement -> toPureLine()
    is SVGCircleElement -> toPureCircle()
    else -> null
}
