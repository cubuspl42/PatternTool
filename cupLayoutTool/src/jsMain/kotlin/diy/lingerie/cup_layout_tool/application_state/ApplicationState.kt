package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.joinOf
import dev.toolkt.reactive.effect.map
import diy.lingerie.cup_layout_tool.application_state.interaction_state.InteractionState

class ApplicationState(
    val documentState: DocumentState,
    val interactionState: InteractionState,
    val simulationState: SimulationState,
    val presentationState: PresentationState,
) {
    companion object {
        fun enter(
            documentState: DocumentState,
        ): Effect<ApplicationState> = InteractionState.enter().joinOf { interactionState ->
            SimulationState.enter().map { simulationState ->
                val presentationState = PresentationState.enter()

                ApplicationState(
                    documentState = documentState,
                    interactionState = interactionState,
                    simulationState = simulationState,
                    presentationState = presentationState,
                )
            }
        }
    }
}
