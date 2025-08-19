package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.reactive.managed_io.ActionContext
import dev.toolkt.reactive.managed_io.Actions

object ReactiveFinalizationRegistry {
    private val finalizationRegistry = PlatformFinalizationRegistry()

    fun register(
        target: Any,
        cleanup: context(ActionContext) () -> Unit,
    ) {
        finalizationRegistry.register(
            target = target,
            cleanup = {
                Actions.external {
                    cleanup()
                }
            },
        )
    }
}
