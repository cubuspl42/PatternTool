package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Actions

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
