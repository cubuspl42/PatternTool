package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.units
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

class CanvasCircleElement(
    override val transformation: Cell<Transformation>? = null,
    override val stroke: Cell<CanvasStroke>?,
    override val fill: Cell<CanvasFill>?,
    private val radius: Cell<Double>,
) : CanvasPathElement() {
    override fun describePath(
        context: CanvasRenderingContext2D,
    ) {
        context.arc(
            x = 0.0,
            y = 0.0,
            radius = radius.currentValueUnmanaged,
            startAngle = 0.0,
            endAngle = 2 * PI,
        )
    }

    override val onPathChanged: EventStream<Unit> = radius.changes.units()
}
