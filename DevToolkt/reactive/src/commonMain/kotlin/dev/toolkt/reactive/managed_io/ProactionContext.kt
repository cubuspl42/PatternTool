package dev.toolkt.reactive.managed_io

interface ProactionContext : ReactionContext {
    companion object {
        context(reactionContext: ReactionContext) fun defer(
            action: context(ProactionContext) () -> Unit,
        ) {
//            reactionContext.enqueueProaction(proaction = action)
        }
    }
}

private class ProactionContextImpl : ProactionContext

object Proactions {
    fun <ResultT> external(
        block: context(ProactionContext) () -> ResultT,
    ): ResultT = with(ProactionContextImpl()) {
        block()
    }
}
