package diy.lingerie.web_tool_3d

import dev.toolkt.dom.reactive.utils.event.offsetPoint
import dev.toolkt.dom.reactive.utils.event.offsetPointNdc
import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.trackMouseOffsetPointNdc
import dev.toolkt.dom.reactive.utils.getKeyDownEventStream
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.managed_io.Program
import dev.toolkt.reactive.managed_io.Schedule
import dev.toolkt.reactive.managed_io.executeCurrentOf
import dev.toolkt.reactive.managed_io.forEachInvoke
import diy.lingerie.web_tool_3d.application_state.InteractionState
import diy.lingerie.web_tool_3d.application_state.PresentationState
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import three.THREE

fun handleInteractionStateEvents(
    canvas: HTMLCanvasElement,
    presentationState: PresentationState,
    interactionState: InteractionState,
    myRenderer: MyRenderer,
): Schedule {
    val cameraRotation = presentationState.cameraRotation

    return Program.parallel(
        handleCameraRotation(
            canvas = canvas,
            cameraRotation = cameraRotation,
        ),
        handleCameraReset(
            presentationState = presentationState,
        ),
        handleManipulationStateEvents(
            canvas = canvas,
            myRenderer = myRenderer,
            interactionState = interactionState,
        ),
    )
}

private fun handleCameraRotation(
    canvas: HTMLCanvasElement,
    cameraRotation: PropertyCell<Double>,
): Schedule = canvas.onMouseDragGestureStarted(
    button = ButtonId.MIDDLE,
).forEachInvoke { mouseGesture ->
    val initialCameraRotation = cameraRotation.currentValue

    val pointerOffset = mouseGesture.trackMouseMovement().map { it.offsetPoint }

    cameraRotation.bindUntil(
        boundValue = pointerOffset.trackTranslation().map { translation ->
            val delta = translation.tx * 0.01

            initialCameraRotation + delta
        },
        until = mouseGesture.onReleased.next().unit(),
    )
}

private fun handleCameraReset(
    presentationState: PresentationState,
): Schedule = document.body!!.getKeyDownEventStream().forEachInvoke {
    if (it.key == "0") {
        presentationState.resetCamera()
    }
}

fun handleManipulationStateEvents(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    interactionState: InteractionState,
): Program<*> = Program.prepare {
    val canvasMouseOffsetPointNdc = canvas.trackMouseOffsetPointNdc()

    interactionState.manipulationState.executeCurrentOf { manipulationStateNow ->
        when (manipulationStateNow) {
            is InteractionState.IdleState -> handleIdleStateEvents(
                canvas = canvas,
                canvasMouseOffsetPointNdc = canvasMouseOffsetPointNdc,
                myRenderer = myRenderer,
                idleState = manipulationStateNow,
            )

            is InteractionState.HandleDragState -> handleHandleDragStateKeyboardEvents(
                canvas = canvas,
                handleDragState = manipulationStateNow,
            )
        }
    }
}

fun handleIdleStateEvents(
    canvas: HTMLCanvasElement,
    canvasMouseOffsetPointNdc: Cell<Point?>,
    myRenderer: MyRenderer,
    idleState: InteractionState.IdleState,
): Program<*> {
    val myScene = myRenderer.myScene

    return Program.parallel(
        canvasMouseOffsetPointNdc.forEachInvoke { offsetPointNdcNow ->
            if (offsetPointNdcNow == null) {
                idleState.clearFocus()

                return@forEachInvoke
            }

            val targetObject = getObjectAtNcdCoord(
                myRenderer = myRenderer,
                ndcCoord = offsetPointNdcNow,
                candidates = myScene.myBezierMesh.handleBalls,
            ) ?: run {
                idleState.clearFocus()

                return@forEachInvoke
            }

            val handleBallUserData = targetObject.myUserData as? MyObjectUserData.HandleBallUserData ?: run {
                idleState.clearFocus()

                return@forEachInvoke
            }

            idleState.focusHandle(
                handle = handleBallUserData.handle,
            )
        },
        handleFocusedHandleStateEvents(
            canvas = canvas,
            myRenderer = myRenderer,
            idleState = idleState,
        ),
    )
}

fun handleFocusedHandleStateEvents(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    idleState: InteractionState.IdleState,
): Program<*> = idleState.focusState.executeCurrentOf { focusStateNow ->
    when (focusStateNow) {
        is InteractionState.FocusedHandleState -> handleFocusedHandleStateEvents(
            canvas = canvas,
            myRenderer = myRenderer,
            focusedHandleState = focusStateNow,
        )

        else -> Program.Noop
    }
}

fun handleFocusedHandleStateEvents(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    focusedHandleState: InteractionState.FocusedHandleState,
): Schedule = canvas.onMouseDragGestureStarted(
    button = ButtonId.LEFT,
).single().forEachInvoke { mouseDragGesture ->
    focusedHandleState.drag(
        targetPosition = findPointerTargetPoint(
            myRenderer = myRenderer,
            handle = focusedHandleState.focusedHandle,
            pointerOffsetCoordNdc = mouseDragGesture.trackMouseMovement().map { it.offsetPointNdc },
        ),
        doCommit = mouseDragGesture.onReleased.units(),
    )
}

fun handleHandleDragStateKeyboardEvents(
    canvas: HTMLCanvasElement,
    handleDragState: InteractionState.HandleDragState,
): Schedule = canvas.getKeyDownEventStream().forEachInvoke {
    if (it.key == "Escape") {
        handleDragState.abort()
    }
}

fun findPointerTargetPoint(
    myRenderer: MyRenderer,
    handle: UserBezierMesh.Handle,
    pointerOffsetCoordNdc: Cell<Point>,
): Cell<Point?> {
    val camera = myRenderer.camera

    val initialPointerOffsetNdc = pointerOffsetCoordNdc.currentValue

    // The initial handle positon in the 2D NDC coordinates
    val initialHandlePositionNdc = handle.worldPosition.currentValue.project(camera = camera).withoutZ()

    // A 2D NDC translation between the grab point and the handle position (constrained)
    val pointerCorrection = initialPointerOffsetNdc.translationTo(target = initialHandlePositionNdc)

    // Corrected pointer offset position by in the 2D NDC coordinates (reactive)
    val correctedPointerOffsetCoordNdc = pointerOffsetCoordNdc.map { offsetCoordNdcNow ->
        pointerCorrection.transform(offsetCoordNdcNow)
    }

    return myRenderer.castRawRay(
        ndcCoord = correctedPointerOffsetCoordNdc,
    ).map { pointerRayNow ->
        val point3D = UserBezierMesh.Handle.plane.findIntersection(pointerRayNow) ?: return@map null

        point3D.withoutZ()
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
