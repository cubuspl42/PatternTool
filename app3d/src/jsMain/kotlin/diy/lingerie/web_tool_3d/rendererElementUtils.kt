package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureBlockStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import diy.lingerie.web_tool_3d.application_state.DocumentState
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun createRendererElement(
    documentState: DocumentState,
): HTMLElement = createResponsiveElement(
    style = ReactiveStyle(
        displayStyle = Cell.Companion.of(
            PureBlockStyle(),
        ),
        width = Cell.Companion.of(100.percent),
        height = Cell.Companion.of(100.percent),
    ),
) { canvasSize ->
    val cameraRotation = PropertyCell(
        initialValue = 0.0,
    )

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
        documentState = documentState,
        viewportSize = canvasSize,
        cameraRotation = cameraRotation,
    )

    val myRenderer = MyRenderer.create(
        canvas = canvas,
        viewportSize = canvasSize,
        myScene = myScene,
    )

    setupInteractionHandlers(
        canvas = canvas,
        documentState = documentState,
        cameraRotation = cameraRotation,
        myRenderer = myRenderer,
    )

    return@createResponsiveElement canvas
}
