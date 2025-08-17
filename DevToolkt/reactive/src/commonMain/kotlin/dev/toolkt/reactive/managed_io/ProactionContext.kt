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

private class ProactionContextImpl : ProactionContext() {
    override fun enqueueReaction(reaction: (ReactionContext) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun enqueueMutation(mutation: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun enqueueProaction(proaction: context(ProactionContext) () -> Unit) {
        TODO("Not yet implemented")
    }
}

object Proactions {
    fun <ResultT> external(
        block: context(ProactionContext) () -> ResultT,
    ): ResultT = with(ProactionContextImpl()) {
        block()
    }
}
