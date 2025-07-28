package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.core.iterable.Untrail
import dev.toolkt.geometry.z
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.future.Future

class InteractionState(
    private val documentState: DocumentState,
) {
    class HandleDragInteraction(
        /**
         * The constrained handle z-value
         */
        val z: Double,

        /**
         * An offset from the handle center to the grab point
         */
        val localOffset: Vector3,

        /**
         * A point which's movement determines the new handle position
         */
        val grabPosition: Cell<Vector3>,
    ) {
        /**
         * The z-value difference between the grab plane and the constrained
         * handle plane
         */
        val localOffsetZ: Double
            get() = localOffset.z

        /**
         * The constrained interaction z-value
         */
        val interactionZ: Double
            get() = z + localOffsetZ

        /**
         * The new dragged handle position determined by this interaction
         */
        val handlePosition: Cell<Vector3> = grabPosition.map { grabPositionNow ->
            grabPositionNow - localOffset
        }
    }

    private val ongoingHandleDragInteraction: MutableCell<HandleDragInteraction?> = MutableCell(
        initialValue = null,
    )

    fun startHandleDragInteraction(
        handlePosition: PropertyCell<Vector3>,
        grabPosition: Cell<Vector3>,
        until: Future<Unit>,
    ) {
        if (ongoingHandleDragInteraction.currentValue != null) return

        val newHandleDragInteraction = HandleDragInteraction(
            z = handlePosition.currentValue.z,
            localOffset = grabPosition.currentValue - handlePosition.currentValue,
            grabPosition = grabPosition,
        )

        handlePosition.bindUntil(
            boundValue = newHandleDragInteraction.handlePosition,
            until = until,
        )
    }
}
