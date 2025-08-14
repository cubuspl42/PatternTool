package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.mergeAllOf
import org.w3c.dom.CanvasRenderingContext2D

class CanvasGroupElement(
    override val transformation: Cell<Transformation>? = null,
    private val children: ReactiveList<CanvasRenderableElement>,
) : CanvasTransformableElement() {
    override fun renderTransformable(context: CanvasRenderingContext2D) {
        children.currentElements.forEach {
            context.save()

            it.render(context = context)

            context.restore()
        }
    }

    override val onTransformableChanged: EventStream<Unit> = children.mergeAllOf {
        it.onChanged
    }
}
