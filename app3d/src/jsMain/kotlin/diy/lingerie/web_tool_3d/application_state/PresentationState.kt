package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.reactive.cell.PropertyCell

class PresentationState {
    val cameraRotation = PropertyCell(
        initialValue = 0.0,
    )
}
