package dev.toolkt.reactive.managed_io

typealias Trigger = Effect<Unit>

context(reactionContext: ReactionContext) fun Trigger.startExternally(): Effect.Handle = start().handle

abstract class TriggerBase : Trigger {
    context(reactionContext: ReactionContext) final override fun start(): Effective<Unit> = Effective(
        result = Unit,
        handle = startInternally(),
    )

    context(reactionContext: ReactionContext) abstract fun startInternally(): Effect.Handle
}

object Triggers {
    object Noop : TriggerBase() {
        context(reactionContext: ReactionContext) override fun startInternally(): Effect.Handle = Effect.Handle.Noop
    }

    context(reactionContext: ReactionContext) fun startAll(
        triggers: Iterable<Trigger>,
    ): Effect.Handle = Effect.Handle.combine(
        handles = triggers.map { it.startExternally() },
    )

    fun combine(
        triggers: Iterable<Trigger>,
    ): Trigger = object : TriggerBase() {
        context(reactionContext: ReactionContext) override fun startInternally() = Effect.Handle.combine(
            handles = triggers.map { it.startExternally() },
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
