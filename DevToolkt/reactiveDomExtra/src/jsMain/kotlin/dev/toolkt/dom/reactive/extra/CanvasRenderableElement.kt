package dev.toolkt.dom.reactive.extra

import dev.toolkt.reactive.event_stream.EventStream
import org.w3c.dom.CanvasRenderingContext2D

abstract class CanvasRenderableElement {
    abstract fun render(
        context: CanvasRenderingContext2D,
    )

    abstract val onChanged: EventStream<Unit>
}


