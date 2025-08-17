package dev.toolkt.reactive.managed_io

typealias Trigger = Effect<Unit>

abstract class TriggerBase : Trigger {
    context(actionContext: ActionContext) final override fun start(): Effective<Unit> = Effective(
        result = Unit,
        handle = jumpStart(),
    )

    context(actionContext: ActionContext) abstract fun jumpStart(): Effect.Handle
}

/**
 * Start the trigger effect, discarding the [Effective.result] (known to be [Unit]).
 */
context(actionContext: ActionContext) fun Trigger.jumpStart(): Effect.Handle = start().handle

object Triggers {
    object Noop : TriggerBase() {
        context(actionContext: ActionContext) override fun jumpStart(): Effect.Handle = Effect.Handle.Noop
    }

    context(actionContext: ActionContext) fun startAll(
        triggers: Iterable<Trigger>,
    ): Effect.Handle = Effect.Handle.combine(
        handles = triggers.map { it.jumpStart() },
    )

    fun combine(
        triggers: Iterable<Trigger>,
    ): Trigger = object : TriggerBase() {
        context(actionContext: ActionContext) override fun jumpStart() = Effect.Handle.combine(
            handles = triggers.map { it.jumpStart() },
        )
    }

    fun combine(
        vararg triggers: Trigger,
    ): Trigger = combine(
        triggers = triggers.asIterable(),
    )
}

context(effectiveContext: EffectiveContext) fun Trigger.defer() {
    effectiveContext.addTrigger(this)
}
