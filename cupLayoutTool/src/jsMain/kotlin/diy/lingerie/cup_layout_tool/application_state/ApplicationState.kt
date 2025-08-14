package diy.lingerie.cup_layout_tool.application_state

class ApplicationState(
    val documentState: DocumentState,
) {
    val presentationState: PresentationState = PresentationState()

    val simulationState: SimulationState = SimulationState()

    val interactionState: InteractionState = InteractionState(
        documentState,
    )
}
