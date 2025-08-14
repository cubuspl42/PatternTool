package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import org.w3c.dom.CanvasRenderingContext2D

class CanvasLineElement(
    override val transformation: Cell<Transformation>? = null,
    override val stroke: Cell<CanvasStroke>?,
    override val fill: Cell<CanvasFill>? = null,
    private val start: Cell<Point>,
    private val end: Cell<Point>,
) : CanvasPathElement() {
    override fun describePath(context: CanvasRenderingContext2D) {
        context.moveTo(start.currentValue)

        context.lineTo(end.currentValue)
    }

    override val onPathChanged: EventStream<Unit> = EventStream.Companion.mergeAll(
        start.newValues,
        end.newValues,
    ).units()
}
