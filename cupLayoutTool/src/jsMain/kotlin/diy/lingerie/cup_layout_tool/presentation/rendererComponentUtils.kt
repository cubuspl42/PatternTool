package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureBlockStyle
import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.dom.reactive.components.HtmlComponent
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivComponent
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.joinOf
import diy.lingerie.cup_layout_tool.application_state.ApplicationState
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

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
    buildChild = { size ->
        createMyCanvasComponent(
            applicationState = applicationState,
            size = size,
        )
    },
)

private fun createMyCanvasComponent(
    applicationState: ApplicationState,
    size: Cell<PureSize>,
): Component<HTMLCanvasElement> {
    val presentationState = applicationState.presentationState
    val interactionState = applicationState.interactionState

    return object : Component<HTMLCanvasElement> {
        override fun buildLeaf(): Effect<HTMLCanvasElement> = document.createReactiveHtmlCanvasComponent(
            style = ReactiveStyle(
                displayStyle = Cell.of(
                    PureBlockStyle(),
                ),
                width = Cell.of(100.percent),
                height = Cell.of(100.percent),
            ),
        ).buildLeaf().joinOf { canvasElement: HTMLCanvasElement ->
            val myScene = MyScene.create(
                applicationState = applicationState,
                viewportSize = size,
                cameraRotation = presentationState.cameraRotation,
            )

            val myRenderer = MyRenderer.create(
                canvas = canvasElement,
                viewportSize = size,
                myScene = myScene,
            )

            Effect.pureTriggering(
                result = canvasElement,
                trigger = handleInteractionStateEvents(
                    canvas = canvasElement,
                    presentationState = presentationState,
                    interactionState = interactionState,
                    myRenderer = myRenderer,
                ),
            )
        }
    }
}
