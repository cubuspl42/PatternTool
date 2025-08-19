package diy.lingerie.cup_layout_tool.presentation

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
import dev.toolkt.reactive.event_stream.holdUnmanaged
import diy.lingerie.cup_layout_tool.UserBezierMesh
import diy.lingerie.cup_layout_tool.application_state.interaction_state.InteractionState
import diy.lingerie.cup_layout_tool.application_state.PresentationState
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import dev.toolkt.js.threejs.THREE
import dev.toolkt.reactive.event_stream.forEach
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.Trigger
import dev.toolkt.reactive.managed_io.Triggers
import dev.toolkt.reactive.managed_io.activateOf
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.ilde_focus_states.FocusedHandleState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.HandleDragState
import diy.lingerie.cup_layout_tool.application_state.interaction_state.manipulation_states.idle_state.IdleState

fun handleInteractionStateEvents(
    canvas: HTMLCanvasElement,
    presentationState: PresentationState,
    interactionState: InteractionState,
    myRenderer: MyRenderer,
): Trigger {
    val cameraRotation = presentationState.cameraRotation

    return Triggers.combine(
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
): Trigger = canvas.onMouseDragGestureStarted(
    button = ButtonId.MIDDLE,
).forEach { mouseGesture ->
    val initialCameraRotation = cameraRotation.currentValueUnmanaged

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
): Trigger = document.body!!.getKeyDownEventStream().forEach {
    if (it.key == "0") {
        presentationState.resetCamera()
    }
}

fun handleManipulationStateEvents(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    interactionState: InteractionState,
): Trigger {
    val canvasMouseOffsetPointNdc = canvas.trackMouseOffsetPointNdc()

    return interactionState.manipulationState.activateOf { manipulationStateNow ->
        when (manipulationStateNow) {
            is IdleState -> handleIdleStateEvents(
                canvas = canvas,
                canvasMouseOffsetPointNdc = canvasMouseOffsetPointNdc,
                myRenderer = myRenderer,
                idleState = manipulationStateNow,
            )

            is HandleDragState -> handleHandleDragStateKeyboardEvents(
                canvas = canvas,
                handleDragState = manipulationStateNow,
            )

            else -> Triggers.Noop
        }
    }
}

fun handleIdleStateEvents(
    canvas: HTMLCanvasElement,
    canvasMouseOffsetPointNdc: Cell<Point?>,
    myRenderer: MyRenderer,
    idleState: IdleState,
): Trigger {
    val myScene = myRenderer.myScene

    val indicatedObject = canvasMouseOffsetPointNdc.map {
        val offsetPointNdcNow = it ?: return@map null

        val targetObject = getObjectAtNcdCoord(
            myRenderer = myRenderer,
            ndcCoord = offsetPointNdcNow,
            candidates = myScene.myBezierMesh.handleBalls,
        ) ?: return@map null

        val handleBallUserData = targetObject.myUserData as? MyObjectUserData.HandleBallUserData ?: return@map null

        IdleState.HandleIndicatedObject(
            handle = handleBallUserData.handle,
        )
    }


    return Effect.prepared {
        idleState.indicatedObject.bind(indicatedObject)

        handleFocusedHandleStateEvents(
            canvas = canvas,
            myRenderer = myRenderer,
            idleState = idleState,
        )
    }
}

fun handleFocusedHandleStateEvents(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    idleState: IdleState,
): Trigger = idleState.focusState.activateOf { focusStateNow ->
    when (focusStateNow) {
        is FocusedHandleState -> handleFocusedHandleStateEvents(
            canvas = canvas,
            myRenderer = myRenderer,
            focusedHandleState = focusStateNow,
        )

        else -> Triggers.Noop
    }
}

fun handleFocusedHandleStateEvents(
    canvas: HTMLCanvasElement,
    myRenderer: MyRenderer,
    focusedHandleState: FocusedHandleState,
): Trigger = Effect.plain {
    focusedHandleState.doDragSlot.bind(
        canvas.onMouseDragGestureStarted(
            button = ButtonId.LEFT,
        ).single().map { mouseDragGesture ->
            FocusedHandleState.DragCommand(
                targetPosition = mouseDragGesture.trackMouseMovement().map { it.offsetPointNdc },
                doCommit = mouseDragGesture.onReleased.units(),
            )
        },
    )
}

fun handleHandleDragStateKeyboardEvents(
    canvas: HTMLCanvasElement,
    handleDragState: HandleDragState,
): Trigger = Effect.plain {
    handleDragState.doAbortSlot.bind(
        canvas.getKeyDownEventStream().filter { it.key == "Escape" }.units(),
    )
}

fun findPointerTargetPoint(
    myRenderer: MyRenderer,
    handle: UserBezierMesh.Handle,
    pointerOffsetCoordNdc: Cell<Point>,
): Cell<Point?> {
    val camera = myRenderer.camera

    val initialPointerOffsetNdc = pointerOffsetCoordNdc.currentValueUnmanaged

    // The initial handle positon in the 2D NDC coordinates
    val initialHandlePositionNdc = handle.worldPosition.currentValueUnmanaged.project(camera = camera).withoutZ()

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
    val initialPoint = currentValueUnmanaged

    return newValues.map {
        initialPoint.translationTo(it)
    }.holdUnmanaged(
        initialValue = PrimitiveTransformation.Translation.None,
    )
}
