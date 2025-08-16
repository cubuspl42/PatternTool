package dev.toolkt.reactive.managed_io

abstract class ProactionContext : MomentContext() {
    companion object {
        context(reactionContext: ReactionContext) fun defer(
            action: context(ProactionContext) () -> Unit,
        ) {
            reactionContext.enqueueProaction(proaction = action)
        }
    }

    abstract fun enqueueReaction(
        reaction: (reactionContext: ReactionContext) -> Unit,
    )
}
