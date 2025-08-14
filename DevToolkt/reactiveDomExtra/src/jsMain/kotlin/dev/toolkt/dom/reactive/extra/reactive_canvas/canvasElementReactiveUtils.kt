package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.core.annotations.NoCapture
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.reactive.DiscardSubscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.listenWeak
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.extra.createCanvasElement
import org.w3c.dom.extra.getContext2D
import kotlin.math.roundToInt

fun createReactiveCanvasElement(
    size: Cell<PureSize>,
    root: CanvasRenderableElement,
): HTMLCanvasElement {
    @NoCapture
    val canvasElement = document.createCanvasElement()

    fun renderContent(
        canvasElement: HTMLCanvasElement,
    ) {
        val context = canvasElement.getContext2D()

        context.save()

        context.clearRect(
            x = 0.0,
            y = 0.0,
            w = canvasElement.width.toDouble(),
            h = canvasElement.height.toDouble(),
        )

        root.render(
            context = context,
        )

        context.restore()
    }

    size.bind(
        target = canvasElement,
    ) { canvasElement, sizeNow ->
        canvasElement.width = sizeNow.width.roundToInt()
        canvasElement.height = sizeNow.height.roundToInt()

        renderContent(
            canvasElement = canvasElement,
        )
    }

    var isRenderAnimationFrameQueued = false

    fun ensureRenderAnimationFrameEnqueued(
        canvasElement: HTMLCanvasElement,
    ) {
        if (isRenderAnimationFrameQueued) {
            return
        }

        window.requestAnimationFrame {
            renderContent(
                canvasElement = canvasElement,
            )

            isRenderAnimationFrameQueued = false
        }

        isRenderAnimationFrameQueued = true
    }

    @DiscardSubscription root.onChanged.listenWeak(
        target = canvasElement,
    ) { canvasElement, _ ->
        ensureRenderAnimationFrameEnqueued(
            canvasElement = canvasElement,
        )
    }

    ensureRenderAnimationFrameEnqueued(
        canvasElement = canvasElement,
    )

    return canvasElement
}
