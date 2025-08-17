package dev.toolkt.reactive.managed_io

abstract class ProactionContext : ReactionContext() {
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

object Proactions {
    fun <ResultT> external(
        block: context(ProactionContext) () -> ResultT,
    ): ResultT {
        TODO()
    }
}
