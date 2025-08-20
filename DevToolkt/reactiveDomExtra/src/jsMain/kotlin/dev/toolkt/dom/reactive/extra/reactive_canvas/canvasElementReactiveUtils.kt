package dev.toolkt.dom.reactive.extra.reactive_canvas

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.forEach
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.forEach
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.extra.getContext2D
import kotlin.math.roundToInt

fun createReactiveCanvasComponent(
    style: ReactiveStyle? = null,
    size: Cell<PureSize>,
    root: CanvasRenderableElement,
): Component<HTMLCanvasElement> = object : Component<HTMLCanvasElement> {
    override fun buildLeaf(): Effect<HTMLCanvasElement> = Effect.prepared {
        val animatedHtmlCanvasElement = createAnimatedReactiveHtmlCanvasElement(
            style,
            render = { context ->
                root.render(
                    context = context,
                )
            },
        )

        val canvasElement = animatedHtmlCanvasElement.canvasElement

        Actions.mutate {
            animatedHtmlCanvasElement.markDirty()
        }

        Effect.pureTriggering(
            result = canvasElement,
            triggers = listOf(
                size.forEach { sizeNow ->
                    Actions.mutate {
                        animatedHtmlCanvasElement.resize(
                            newSize = sizeNow,
                        )
                    }
                },
                root.onChanged.forEach {
                    Actions.mutate {
                        Actions.mutate {
                            animatedHtmlCanvasElement.markDirty()
                        }
                    }
                },
            ),
        )
    }
}

private interface AnimatedHtmlCanvasElement {
    val canvasElement: HTMLCanvasElement

    fun resize(
        newSize: PureSize,
    )

    fun markDirty()
}

private context(momentContext: MomentContext) fun createAnimatedReactiveHtmlCanvasElement(
    style: ReactiveStyle? = null,
    render: (context: CanvasRenderingContext2D) -> Unit,
): AnimatedHtmlCanvasElement {
    val canvasElement = document.createReactiveHtmlCanvasElement(
        style = style,
    )

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

        render(context)

        context.restore()
    }

    var isRenderAnimationFrameQueued = false

    return object : AnimatedHtmlCanvasElement {
        override val canvasElement: HTMLCanvasElement
            get() = canvasElement

        override fun resize(newSize: PureSize) {
            canvasElement.width = newSize.width.roundToInt()
            canvasElement.height = newSize.height.roundToInt()

            markDirty()
        }

        override fun markDirty() {
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
    }
}
