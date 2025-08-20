package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.joinOf
import dev.toolkt.reactive.managed_io.map
import diy.lingerie.cup_layout_tool.application_state.interaction_state.InteractionState

class ApplicationState(
    val documentState: DocumentState,
    val interactionState: InteractionState,
    val simulationState: SimulationState,
) {
    companion object {
        fun enter(
            documentState: DocumentState,
        ): Effect<ApplicationState> = InteractionState.enter().joinOf { interactionState ->
            SimulationState.enter().map { simulationState ->
                ApplicationState(
                    documentState = documentState,
                    interactionState = interactionState,
                    simulationState = simulationState,
                )
            }
        }
    }

    val presentationState: PresentationState = PresentationState()
}
