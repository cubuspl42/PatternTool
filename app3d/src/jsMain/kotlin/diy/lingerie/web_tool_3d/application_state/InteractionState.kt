package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.future.Future

class InteractionState(
    private val documentState: DocumentState,
) {
    class HandleDragInteraction(
        val requestedHandlePosition: Cell<Point>,
    )

    private val ongoingHandleDragInteraction: MutableCell<HandleDragInteraction?> = MutableCell(
        initialValue = null,
    )

    fun startHandleDragInteraction(
        handlePosition: PropertyCell<Point>,
        requestedHandlePosition: Cell<Point>,
        until: Future<Unit>,
    ): HandleDragInteraction? {
        if (ongoingHandleDragInteraction.currentValue != null) return null

        val newHandleDragInteraction = HandleDragInteraction(
            requestedHandlePosition = requestedHandlePosition,
        )

        ongoingHandleDragInteraction.set(newHandleDragInteraction)

        until.onFulfilled.forEach {
            ongoingHandleDragInteraction.set(null)
        }

        handlePosition.bindUntil(
            boundValue = requestedHandlePosition,
            until = until,
        )

        return newHandleDragInteraction
    }
}
