package diy.lingerie.web_tool

external class FinalizationRegistry(
    cleanupCallback: (heldValue: dynamic) -> Unit,
) {
    fun register(
        target: dynamic,
        heldValue: dynamic,
        unregisterToken: dynamic = definedExternally,
    )

    fun unregister(
        unregisterToken: dynamic,
    )
}
