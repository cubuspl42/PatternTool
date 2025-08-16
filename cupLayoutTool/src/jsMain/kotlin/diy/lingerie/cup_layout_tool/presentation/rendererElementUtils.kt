package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureBlockStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.managed_io.ReactionContext
import dev.toolkt.reactive.managed_io.startBound
import diy.lingerie.cup_layout_tool.application_state.ApplicationState
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

context(reactionContext: ReactionContext)
fun createRendererElement(
    applicationState: ApplicationState,
): HTMLElement = createResponsiveElement(
    createGrowingWrapper = { wrappedChildren ->
        document.createReactiveHtmlDivElement(
            style = ReactiveStyle(
                displayStyle = Cell.Companion.of(
                    PureBlockStyle(),
                ),
                width = Cell.Companion.of(100.percent),
                height = Cell.Companion.of(100.percent),
            ),
            children = wrappedChildren,
        )
    },
    buildChild = { canvasSize ->
        val presentationState = applicationState.presentationState
        val interactionState = applicationState.interactionState

        document.createReactiveHtmlCanvasElement(
            style = ReactiveStyle(
                displayStyle = Cell.Companion.of(
                    PureBlockStyle(),
                ),
                width = Cell.of(100.percent),
                height = Cell.of(100.percent),
            ),
        ).also { canvas ->
            val myScene = MyScene.create(
                applicationState = applicationState,
                viewportSize = canvasSize,
                cameraRotation = presentationState.cameraRotation,
            )

            val myRenderer = MyRenderer.create(
                canvas = canvas,
                viewportSize = canvasSize,
                myScene = myScene,
            )

            handleInteractionStateEvents(
                canvas = canvas,
                presentationState = presentationState,
                interactionState = interactionState,
                myRenderer = myRenderer,
            ).startBound(
                target = canvas,
            )
        }
    },
)
