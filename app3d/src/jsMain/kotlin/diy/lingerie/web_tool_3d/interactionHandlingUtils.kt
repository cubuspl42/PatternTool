package diy.lingerie.web_tool_3d

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.reactive.cell.PropertyCell
import org.w3c.dom.HTMLCanvasElement

fun setupInteractionHandlers(
    canvas: HTMLCanvasElement,
    userSystem: UserSystem,
    cameraRotation: PropertyCell<Double>,
    myRenderer: MyRenderer,
) {
    val myScene = myRenderer.myScene

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

        PlatformSystem.log(intersection?.`object`?.myUserData as? MyObjectUserData.HandleBall)
    }
}
