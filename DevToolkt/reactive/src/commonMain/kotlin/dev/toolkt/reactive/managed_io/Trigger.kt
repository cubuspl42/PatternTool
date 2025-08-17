package dev.toolkt.reactive.managed_io

typealias Trigger = Effect<Unit>

abstract class TriggerBase : Trigger {
    context(reactionContext: ReactionContext) final override fun start(): Effective<Unit> = Effective(
        result = Unit,
        handle = jumpStart(),
    )

    context(reactionContext: ReactionContext) abstract fun jumpStart(): Effect.Handle
}

/**
 * Start the trigger effect, discarding the [Effective.result] (known to be [Unit]).
 */
context(reactionContext: ReactionContext) fun Trigger.jumpStart(): Effect.Handle = start().handle

object Triggers {
    object Noop : TriggerBase() {
        context(reactionContext: ReactionContext) override fun jumpStart(): Effect.Handle = Effect.Handle.Noop
    }

    context(reactionContext: ReactionContext) fun startAll(
        triggers: Iterable<Trigger>,
    ): Effect.Handle = Effect.Handle.combine(
        handles = triggers.map { it.jumpStart() },
    )

    fun combine(
        triggers: Iterable<Trigger>,
    ): Trigger = object : TriggerBase() {
        context(reactionContext: ReactionContext) override fun jumpStart() = Effect.Handle.combine(
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
