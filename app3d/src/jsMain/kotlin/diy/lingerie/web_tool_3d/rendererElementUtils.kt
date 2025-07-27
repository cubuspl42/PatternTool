package diy.lingerie.web_tool_3d

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureBlockStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveElement
import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasElement
import dev.toolkt.geometry.Vector3
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun createRendererElement(
    userSystem: UserSystem,
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
        userSystem = userSystem,
        viewportSize = canvasSize,
        cameraRotation = cameraRotation,
    )

    val myRenderer = MyRenderer.create(
        canvas = canvas,
        viewportSize = canvasSize,
        myScene = myScene,
    )

    canvas.onMouseDragGestureStarted(
        button = ButtonId.MIDDLE,
    ).forEach { mouseGesture ->
        val initialCameraRotation = cameraRotation.currentValue

        cameraRotation.bindUntil(
            boundValue = mouseGesture.offsetPosition.trackTranslation().map { translation ->
                val delta = translation.tx * 0.01

                initialCameraRotation + delta
            },
            until = mouseGesture.onFinished,
        )

        mouseGesture.offsetPosition
    }

    canvas.onMouseDragGestureStarted(
        button = ButtonId.LEFT,
    ).forEach { mouseGesture ->
        val intersection = myRenderer.castRay(
            viewportPoint = mouseGesture.offsetPosition.currentValue,
            objects = myScene.myBezierMesh.handleBalls,
        )

        PlatformSystem.log(intersection?.`object`)
    }

    return@createResponsiveElement canvas
}
