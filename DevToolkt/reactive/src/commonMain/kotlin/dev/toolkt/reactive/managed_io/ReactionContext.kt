package dev.toolkt.reactive.managed_io

interface ReactionContext : MomentContext

private class ReactionContextImpl : ReactionContext {
    companion object {
        fun <ResultT> execute(
            block: context(ReactionContext) () -> ResultT,
        ): ResultT {
            TODO()
        }
    }

    private val enqueuedProactions = mutableListOf<context(ProactionContext) () -> Unit>()

    private val enqueuedMutations = mutableListOf<() -> Unit>()

    fun finish() {
        enqueuedProactions.forEach { proaction ->
            TODO()
        }
    }
}

object Reactions {
    fun <ResultT> external(
        block: context(ReactionContext) () -> ResultT,
    ): ResultT = with(ReactionContextImpl()) {
        block()
    }

    context(reactionContext: ReactionContext) fun defer(
        proaction: context(ProactionContext) () -> Unit,
    ) {
//        reactionContext.enqueueProaction(
//            proaction = proaction,
//        )
    }
}
