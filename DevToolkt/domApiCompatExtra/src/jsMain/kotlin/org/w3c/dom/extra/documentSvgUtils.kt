package org.w3c.dom.extra

import org.w3c.dom.Document
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPolylineElement
import org.w3c.dom.svg.SVGSVGElement

private const val svgNamespace = "http://www.w3.org/2000/svg"

fun Document.createSvgCircleElement(): SVGCircleElement = createSvgElement("circle") as SVGCircleElement

fun Document.createSvgPathElement(): SVGPathElement = createSvgElement("path") as SVGPathElement

fun Document.createSvgPolylineElement(): SVGPolylineElement = createSvgElement("polyline") as SVGPolylineElement

fun Document.createSvgLineElement(): SVGLineElement = createSvgElement("line") as SVGLineElement

fun Document.createSvgGroupElement(): SVGGElement = createSvgElement("g") as SVGGElement

fun Document.createSvgSvgElement(): SVGSVGElement = createSvgElement("svg") as SVGSVGElement

private fun Document.createSvgElement(
    localName: String,
): SVGElement = createElementNS(
    namespace = svgNamespace,
    qualifiedName = localName,
) as SVGElement
