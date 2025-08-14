package dev.toolkt.dom.reactive.extra

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

class CanvasCircleElement(
    override val transformation: Cell<Transformation>? = null,
    private val radius: Cell<Double>,
) : CanvasTransformableElement() {
    override fun renderTransformed(context: CanvasRenderingContext2D) {
        context.beginPath()

        context.arc(
            x = 0.0,
            y = 0.0,
            radius = radius.currentValue,
            startAngle = 0.0,
            endAngle = 2 * PI,
        )

        context.fill()
    }

    override val onContentChanged: EventStream<Unit> = radius.changes.units()
}
