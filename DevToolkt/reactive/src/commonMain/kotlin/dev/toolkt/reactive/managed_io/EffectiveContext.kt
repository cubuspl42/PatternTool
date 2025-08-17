package dev.toolkt.reactive.managed_io

abstract class EffectiveContext {
    companion object {
        context(actionContext: EffectiveContext) fun deferCtx(
            trigger: Trigger,
        ) {
            actionContext.addTrigger(trigger)
        }
    }

    abstract fun addTrigger(
        trigger: Trigger,
    )
}

context(actionContext: EffectiveContext) operator fun Trigger.unaryPlus() {
    actionContext.addTrigger(this)
}
