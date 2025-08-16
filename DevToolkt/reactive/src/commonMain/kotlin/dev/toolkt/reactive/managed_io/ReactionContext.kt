package dev.toolkt.reactive.managed_io

abstract class ReactionContext : MomentContext() {
    object Placeholder : ReactionContext() {

    }

    fun enqueueMutation(
        mutation: () -> Unit,
    ) {
        TODO()
    }

    fun enqueueProaction(
        proaction: context (ProactionContext) () -> Unit,
    ) {
        TODO()
    }
}

object Reactions {
    context(reactionContext: ReactionContext) fun defer(
        action: context(ProactionContext) () -> Unit,
    ) {
        TODO()
    }
}
