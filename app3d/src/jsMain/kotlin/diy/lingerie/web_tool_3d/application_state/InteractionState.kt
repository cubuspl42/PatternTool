package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.future.Future

class InteractionState(
    private val documentState: DocumentState,
) {
    class HandleDragInteraction(
        /**
         * A point which's movement determines the new handle position
         */
        val requestedHandlePosition: Cell<Point>,
    )

    // TODO: Re-enable this code
//    private val ongoingHandleDragInteraction: MutableCell<HandleDragInteraction?> = MutableCell(
//        initialValue = null,
//    )

    fun startHandleDragInteraction(
        handlePosition: PropertyCell<Point>,
        requestedHandlePosition: Cell<Point>,
        until: Future<Unit>,
    ) {
//        if (ongoingHandleDragInteraction.currentValue != null) return

//        val newHandleDragInteraction = HandleDragInteraction(
//            requestedHandlePosition = requestedHandlePosition,
//        )

//        ongoingHandleDragInteraction.set(newHandleDragInteraction)

//        until.onFulfilled.forEach {
//            ongoingHandleDragInteraction.set(null)
//        }

        handlePosition.bindUntil(
            boundValue = requestedHandlePosition,
            until = until,
        )
    }
}
