package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureBlockStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveFlexElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.managed_io.executeBound
import diy.lingerie.web_tool_3d.application_state.ApplicationState
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun createRendererElement(
    applicationState: ApplicationState,
): HTMLElement = createResponsiveFlexElement(
    style = ReactiveStyle(
        displayStyle = Cell.Companion.of(
            PureBlockStyle(),
        ),
        width = Cell.Companion.of(100.percent),
        height = Cell.Companion.of(100.percent),
    ),
) { canvasSize ->
    val presentationState = applicationState.presentationState
    val interactionState = applicationState.interactionState

    val canvas = document.createReactiveHtmlCanvasElement(
        style = ReactiveStyle(
            displayStyle = Cell.Companion.of(
                PureBlockStyle(),
            ),
            width = Cell.Companion.of(100.percent),
            height = Cell.Companion.of(100.percent),
        ),
    )

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
    ).executeBound(
        target = canvas,
    )

    return@createResponsiveFlexElement canvas
}
