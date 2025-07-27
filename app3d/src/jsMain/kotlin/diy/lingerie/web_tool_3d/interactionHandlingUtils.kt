package diy.lingerie.web_tool_3d

import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.geometry.xy
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import org.w3c.dom.HTMLCanvasElement
import three.THREE
import three.localize

fun setupInteractionHandlers(
    canvas: HTMLCanvasElement,
    userSystem: UserSystem,
    cameraRotation: PropertyCell<Double>,
    myRenderer: MyRenderer,
) {
    val myScene = myRenderer.myScene
    val floor: THREE.Object3D = myScene.floor

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
        ) ?: return@forEach

        val handleBallUserData =
            intersection.`object`.myUserData as? MyObjectUserData.HandleBallUserData ?: return@forEach

        val position: PropertyCell<Vector2> = handleBallUserData.position

        val floorLocalPoint: Cell<THREE.Intersection?> = myRenderer.castRay(
            viewportPoint = mouseGesture.offsetPosition,
            objects = listOf(floor),
        )

        val newCorrectedLocalPoints = floorLocalPoint.newValues.mapNotNull {
            val worldPoint = it?.point ?: return@mapNotNull null
            val localPoint = floor.localize(worldPoint = worldPoint).toMathVector3()
            localPoint.xy
        }

        position.bindUntil(
            newValues = newCorrectedLocalPoints,
            until = mouseGesture.onFinished,
        )
    }
}
