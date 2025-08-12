package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import org.w3c.dom.svg.SVGCircleElement

fun SVGCircleElement.toPureCircle(): PureSvgCircle = PureSvgCircle(
    center = Point(
        x = cx.baseVal.value.toDouble(),
        y = cy.baseVal.value.toDouble(),
    ),
    radius = r.baseVal.value.toDouble(),
    stroke = extractStroke(),
)
