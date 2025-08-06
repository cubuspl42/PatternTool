package diy.lingerie.web_tool_3d

import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.dom.reactive.utils.getKeyDownEventStream
import dev.toolkt.geometry.Plane
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.event_stream.hold
import diy.lingerie.web_tool_3d.application_state.InteractionState
import diy.lingerie.web_tool_3d.application_state.PresentationState
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import three.THREE
import three.worldPosition

fun setupInteractionHandlers(
    canvas: HTMLCanvasElement,
    presentationState: PresentationState,
    interactionState: InteractionState,
    myRenderer: MyRenderer,
) {
    val cameraRotation = presentationState.cameraRotation
    val myScene = myRenderer.myScene
    val camera = myScene.camera

    setupCameraRotationHandler(
        canvas = canvas,
        cameraRotation = cameraRotation,
    )

    setupDragHandler(
        canvas = canvas,
        myRenderer = myRenderer,
        myScene = myScene,
        camera = camera,
        interactionState = interactionState,
    )

    setupCameraResetHandler(
        presentationState = presentationState,
    )
}

private fun setupCameraRotationHandler(
    canvas: HTMLCanvasElement,
    cameraRotation: PropertyCell<Double>,
) {
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
}

private fun setupDragHandler(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    myScene: MyScene,
    camera: THREE.PerspectiveCamera,
    interactionState: InteractionState,
) {
    canvas.onMouseDragGestureStarted(
        button = ButtonId.LEFT,
    ).forEach { mouseGesture ->
        // The initial pointer offset in the 2D NDC coordinates
        val initialPointerOffsetNdc = mouseGesture.offsetPositionNdc.currentValue

        // The 3D object at the initial pointer offset in the 2D NDC coordinates
        // If there's no object at that position, abort
        val targetObject = getObjectAtNcdCoord(
            myRenderer = myRenderer,
            ndcCoord = initialPointerOffsetNdc,
            // Consider only the handle ball objects
            candidates = myScene.myBezierMesh.handleBalls,
        ) ?: return@forEach

        // The handle ball user data
        // If the object is not a handle ball (which is not expected, as we considered only handle balls), abort
        val handleBallUserData = targetObject.myUserData as? MyObjectUserData.HandleBallUserData ?: return@forEach

        // The handle manipulated by the handle ball
        val handle: UserBezierMesh.Handle = handleBallUserData.handle

        // The initial handle positon in the 2D NDC coordinates
        val initialHandlePositionNdc = targetObject.worldPosition.toPoint3D().project(camera = camera).withoutZ()

        // A 2D NDC translation between the grab point and the handle position (constrained)
        val grabTranslation = initialPointerOffsetNdc.translationTo(target = initialHandlePositionNdc)

        // Corrected pointer offset position by in the 2D NDC coordinates (reactive)
        val correctedPointerOffsetNdc = mouseGesture.offsetPositionNdc.map { offsetPositionNdcNow ->
            grabTranslation.transform(offsetPositionNdcNow)
        }

        // The grab plane is simply the XY plane (Z = 0)
        val grabPlane = Plane.Xy

        interactionState.startHandleDragInteraction(
            handle = handle,
            requestedHandlePosition = myRenderer.castRawRay(
                ndcCoord = correctedPointerOffsetNdc,
            ).map { pointerRayNow ->
                // The image of the (corrected) pointer offset on the grab plane
                val grabPointNow = grabPlane.findIntersection(pointerRayNow) ?: run {
                    // If we point towards "the sky", there won't be any intersection at all
                    return@map Point.origin
                }

                grabPointNow.withoutZ()
            },
            until = mouseGesture.onFinished,
        )
    }
}

private fun setupCameraResetHandler(
    presentationState: PresentationState,
) {
    document.body!!.getKeyDownEventStream().forEach {
        if (it.key == "0") {
            presentationState.resetCamera()
        }
    }
}

private fun getObjectAtNcdCoord(
    myRenderer: MyRenderer,
    candidates: List<THREE.Object3D>,
    ndcCoord: Point,
): THREE.Object3D? {
    val intersection = myRenderer.castRay(
        ndcCoord = ndcCoord,
        objects = candidates,
    ) ?: return null

    return intersection.object_
}

private fun Cell<Point>.trackTranslation(): Cell<PrimitiveTransformation.Translation> {
    val initialPoint = currentValue

    return newValues.map {
        initialPoint.translationTo(it)
    }.hold(
        initialValue = PrimitiveTransformation.Translation.None,
    )
}
