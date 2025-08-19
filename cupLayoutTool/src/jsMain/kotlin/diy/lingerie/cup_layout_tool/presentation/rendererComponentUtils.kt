package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureBlockStyle
import dev.toolkt.dom.reactive.components.HtmlComponent
import dev.toolkt.dom.reactive.components.alsoTriggering
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivComponent
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.cup_layout_tool.application_state.ApplicationState
import kotlinx.browser.document

fun createRendererComponent(
    applicationState: ApplicationState,
): HtmlComponent = createResponsiveComponent(
    createGrowingWrapper = { wrappedChildren ->
        document.createReactiveHtmlDivComponent(
            style = ReactiveStyle(
                displayStyle = Cell.Companion.of(
                    PureBlockStyle(),
                ),
                width = Cell.of(100.percent),
                height = Cell.of(100.percent),
            ),
            children = wrappedChildren,
        )
    },
    buildChild = { canvasSize ->
        val presentationState = applicationState.presentationState
        val interactionState = applicationState.interactionState

        document.createReactiveHtmlCanvasComponent(
            style = ReactiveStyle(
                displayStyle = Cell.of(
                    PureBlockStyle(),
                ),
                width = Cell.of(100.percent),
                height = Cell.of(100.percent),
            ),
        ).alsoTriggering { canvas ->
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
            )
        }
    },
)
