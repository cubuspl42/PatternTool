package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.reactive.event_stream.EventStream
import org.w3c.dom.CanvasRenderingContext2D

abstract class CanvasRenderableElement {
    /**
     * Renders the element to the given [context], assuming that the parent
     * transformation has already been applied.
     */
    internal abstract fun render(
        context: CanvasRenderingContext2D,
    )

    internal abstract val onChanged: EventStream<Unit>
}
