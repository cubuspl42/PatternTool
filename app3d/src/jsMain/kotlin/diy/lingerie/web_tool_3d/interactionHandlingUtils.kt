package diy.lingerie.web_tool_3d

import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.dom.reactive.utils.getKeyDownEventStream
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.event_stream.hold
import diy.lingerie.web_tool_3d.application_state.InteractionState
import diy.lingerie.web_tool_3d.application_state.PresentationState
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

fun setupInteractionHandlers(
    canvas: HTMLCanvasElement,
    presentationState: PresentationState,
    interactionState: InteractionState,
    myRenderer: MyRenderer,
) {
    val cameraRotation = presentationState.cameraRotation

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
        ) ?: return@forEach

        val handleBallUserData =
            intersection.`object`.myUserData as? MyObjectUserData.HandleBallUserData ?: return@forEach

        val handlePosition: PropertyCell<Point> = handleBallUserData.position

        val initialGrabPosition = intersection.point.toPoint3D()

//        println("initialGrabPosition:")
//        println(initialGrabPosition)

        // A 2D translation between the grab point and the desired handle position
        val initialHandlePosition = handlePosition.currentValue

//        println("initialHandlePosition:")
//        println(initialHandlePosition)

        val grabTranslation = initialGrabPosition.withoutZ().translationTo(
            target = initialHandlePosition,
        )

//        println("grabTranslation:")
//        println(grabTranslation)

        val grabPlane = initialGrabPosition.xyPlane

        interactionState.startHandleDragInteraction(
            handlePosition = handlePosition,
            requestedHandlePosition = myRenderer.castRawRay(
                viewportPoint = mouseGesture.offsetPosition,
            ).map { pointerRayNow ->
                // A ray cast from camera is unlikely to be parallel to the grab plane
                val grabPointNow = grabPlane.findIntersection(pointerRayNow) ?: Point3D.origin

                val requestedHandlePoint = grabPointNow.withoutZ().transformBy(grabTranslation)

//                println("requestedHandlePoint:")
//                println(requestedHandlePoint)

                requestedHandlePoint
            },
            until = mouseGesture.onFinished,
        )
    }

    document.body!!.getKeyDownEventStream().forEach {
        if (it.key == "0") {
            presentationState.resetCamera()
        }
    }
}

private fun Cell<Point>.trackTranslation(): Cell<PrimitiveTransformation.Translation> {
    val initialPoint = currentValue

    return newValues.map {
        initialPoint.translationTo(it)
    }.hold(
        initialValue = PrimitiveTransformation.Translation.None,
    )
}
