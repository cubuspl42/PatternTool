package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.core.iterable.uncons
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.CanvasRenderingContext2D

class CanvasPolylineElement(
    override val transformation: Cell<Transformation>? = null,
    override val stroke: Cell<CanvasStroke>?,
    override val fill: Cell<CanvasFill>? = null,
    private val points: ReactiveList<Point>,
) : CanvasPathElement() {
    override fun describePath(context: CanvasRenderingContext2D) {
        val (firstPoint, trailingPoints) = points.currentElements.uncons() ?: return

        context.moveTo(firstPoint)

        for (point in trailingPoints) {
            context.lineTo(point)
        }
    }

    override val onPathChanged: EventStream<Unit> = points.changes.units()
}
