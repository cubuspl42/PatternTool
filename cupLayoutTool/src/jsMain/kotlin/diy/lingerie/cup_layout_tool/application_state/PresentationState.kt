package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.reactive.cell.PropertyCell

class PresentationState {
    val cameraRotation = PropertyCell(
        initialValue = 0.0,
    )

    fun resetCamera() {
        val cameraRotationState = cameraRotation.state.currentValueUnmanaged as? PropertyCell.State.Unbound ?: return

        cameraRotationState.set(0.0)
    }
}
