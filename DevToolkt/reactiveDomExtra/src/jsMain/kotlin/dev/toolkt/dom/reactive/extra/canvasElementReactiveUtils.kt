package dev.toolkt.dom.reactive.extra

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
    val canvasElement = document.createCanvasElement()

    size.bind(
        target = canvasElement,
    ) { canvasElement, sizeNow ->
        canvasElement.width = sizeNow.width.roundToInt()
        canvasElement.height = sizeNow.height.roundToInt()

        root.render(
            context = canvasElement.getContext2D(),
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
            root.render(
                context = canvasElement.getContext2D(),
            )

            isRenderAnimationFrameQueued = false
        }

        isRenderAnimationFrameQueued = true
    }

    @DiscardSubscription
    root.onChanged.listenWeak(
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
