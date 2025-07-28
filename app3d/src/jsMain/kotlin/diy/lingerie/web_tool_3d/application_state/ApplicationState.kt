package diy.lingerie.web_tool_3d.application_state

class ApplicationState(
    val documentState: DocumentState,
) {
    val presentationState: PresentationState = PresentationState()

    val interactionState: InteractionState = InteractionState()
}
