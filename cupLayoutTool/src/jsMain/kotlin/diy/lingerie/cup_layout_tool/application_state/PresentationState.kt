package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.ActionContext

class PresentationState {
    val cameraRotation = PropertyCell(
        initialValue = 0.0,
    )

    context(actionContext: ActionContext) fun resetCamera() {
        val cameraRotationState = cameraRotation.state.currentValueUnmanaged as? PropertyCell.State.Unbound ?: return

        cameraRotationState.set(0.0)
    }
}
