package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import org.w3c.dom.svg.SVGLineElement

fun SVGLineElement.toPureLine(): PureSvgLine = PureSvgLine(
    start = Point(
        x = x1.baseVal.value.toDouble(),
        y = y1.baseVal.value.toDouble(),
    ),
    end = Point(
        x = x2.baseVal.value.toDouble(),
        y = y2.baseVal.value.toDouble(),
    ),
    stroke = extractStroke(),
)

