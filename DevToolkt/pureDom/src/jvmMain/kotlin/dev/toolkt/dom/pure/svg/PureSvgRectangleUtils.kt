package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Size
import org.w3c.dom.svg.SVGRectElement

fun SVGRectElement.toPureRect(): PureSvgRectangle = PureSvgRectangle(
    position = Point(
        x = x.baseVal.value.toDouble(),
        y = y.baseVal.value.toDouble(),
    ),
    size = Size(
        width = width.baseVal.value.toDouble(),
        height = height.baseVal.value.toDouble(),
    ),
    stroke = extractStroke(),
)
