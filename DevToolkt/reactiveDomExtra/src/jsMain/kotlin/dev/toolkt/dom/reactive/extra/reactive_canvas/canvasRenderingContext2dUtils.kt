package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point
import org.w3c.dom.CanvasRenderingContext2D

fun CanvasRenderingContext2D.moveTo(p: Point) {
    moveTo(p.x, p.y)
}

fun CanvasRenderingContext2D.lineTo(p: Point) {
    lineTo(p.x, p.y)
}

fun CanvasRenderingContext2D.setStrokeStyle(
    color: PureColor,
) {
    this.strokeStyle = color.cssString
}

fun CanvasRenderingContext2D.setFillStyle(
    color: PureColor,
) {
    this.fillStyle = color.cssString
}
