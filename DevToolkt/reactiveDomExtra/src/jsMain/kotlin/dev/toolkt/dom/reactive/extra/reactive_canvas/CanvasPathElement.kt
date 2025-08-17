package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mergeWith
import org.w3c.dom.CanvasRenderingContext2D

abstract class CanvasPathElement : CanvasTransformableElement() {
    value class CanvasStroke(
        val color: PureColor,
    ) {
        companion object {
            val Default = CanvasStroke(
                color = PureColor.black,
            )
        }
    }

    value class CanvasFill(
        val color: PureColor,
    )

    final override fun renderTransformable(context: CanvasRenderingContext2D) {
        val strokeNow = stroke?.currentValueUnmanaged

        strokeNow?.let { strokeNow ->
            context.setStrokeStyle(color = strokeNow.color)
        }

        val fillNow = fill?.currentValueUnmanaged

        fillNow?.let { fillNow ->
            context.setFillStyle(color = fillNow.color)
        }

        context.beginPath()

        describePath(context = context)

        if (fillNow != null) {
            context.fill()
        }

        if (strokeNow != null) {
            context.stroke()
        }
    }

    final override val onTransformableChanged: EventStream<Unit> by lazy {
        val paramChanges = EventStream.mergeAll(
            sources = listOfNotNull(
                stroke?.newValues,
                fill?.newValues,
            ),
        )

        onPathChanged.mergeWith(paramChanges).units()
    }

    protected abstract val stroke: Cell<CanvasStroke>?

    protected abstract val fill: Cell<CanvasFill>?

    /**
     * Describes the path to be rendered in the given [context] by issuing
     * the appropriate drawing commands.
     */
    protected abstract fun describePath(
        context: CanvasRenderingContext2D,
    )

    protected abstract val onPathChanged: EventStream<Unit>
}
