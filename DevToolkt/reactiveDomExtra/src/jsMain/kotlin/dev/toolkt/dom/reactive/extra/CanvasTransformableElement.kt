package dev.toolkt.dom.reactive.extra

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mergeWith
import org.w3c.dom.CanvasRenderingContext2D

abstract class CanvasTransformableElement : CanvasRenderableElement() {
    final override fun render(
        context: CanvasRenderingContext2D,
    ) {
        transformation?.currentValue?.toUniversal?.let { transformationNow ->
            context.transform(
                a = transformationNow.a,
                b = transformationNow.b,
                c = transformationNow.c,
                d = transformationNow.d,
                e = transformationNow.tx,
                f = transformationNow.ty,
            )
        }

        renderTransformed(context = context)
    }

    final override val onChanged: EventStream<Unit> by lazy {
        val transformationChanges = transformation?.changes ?: EventStream.Never

        onContentChanged.mergeWith(transformationChanges).units()
    }

    protected abstract val transformation: Cell<Transformation>?

    abstract fun renderTransformed(
        context: CanvasRenderingContext2D,
    )

    abstract val onContentChanged: EventStream<Unit>
}
