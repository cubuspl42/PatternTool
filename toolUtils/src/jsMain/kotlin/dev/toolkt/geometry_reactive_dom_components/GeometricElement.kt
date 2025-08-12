package dev.toolkt.geometry_reactive_dom_components

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.svg.SVGElement

abstract class GeometricElement {
    data class UiBuildContext(
        val transformation: Cell<Transformation>,
    )

    abstract fun draw(
        renderingContext: CanvasRenderingContext2D,
    )

    abstract fun buildUi(
        buildContext: UiBuildContext,
    ): SVGElement?
}

