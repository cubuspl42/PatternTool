package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.map
import diy.lingerie.cup_layout_tool.application_state.interaction_state.InteractionState

class ApplicationState(
    val documentState: DocumentState,
    val interactionState: InteractionState,
) {
    companion object {
        fun enter(
            documentState: DocumentState,
        ): Effect<ApplicationState> = InteractionState.enter().map { interactionState ->
            ApplicationState(
                documentState = documentState,
                interactionState = interactionState,
            )
        }
    }

    val presentationState: PresentationState = PresentationState()

    val simulationState: SimulationState = SimulationState()
}
