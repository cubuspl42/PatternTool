package dev.toolkt.reactive.managed_io

abstract class EffectiveContext {
    companion object {
        context(proactionContext: EffectiveContext) fun deferCtx(
            trigger: Trigger,
        ) {
            proactionContext.addTrigger(trigger)
        }
    }

    abstract fun addTrigger(
        trigger: Trigger,
    )
}

context(proactionContext: EffectiveContext) operator fun Trigger.unaryPlus() {
    proactionContext.addTrigger(this)
}
