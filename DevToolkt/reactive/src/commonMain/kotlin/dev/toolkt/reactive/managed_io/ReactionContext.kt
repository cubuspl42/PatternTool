package dev.toolkt.reactive.managed_io

abstract class ReactionContext : MomentContext() {
    internal abstract fun enqueueProaction(
        proaction: context (ProactionContext) () -> Unit,
    )

    internal abstract fun enqueueMutation(
        mutation: () -> Unit,
    )
}

private class ReactionContextImpl : ReactionContext() {
    companion object {
        fun <ResultT> execute(
            block: context(ReactionContext) () -> ResultT,
        ): ResultT {
            TODO()
        }
    }

    private val enqueuedProactions = mutableListOf<context(ProactionContext) () -> Unit>()

    private val enqueuedMutations = mutableListOf<() -> Unit>()

    override fun enqueueProaction(
        proaction: context(ProactionContext) () -> Unit,
    ) {
        enqueuedProactions.add(proaction)
    }

    // Shouldn't this be present only in the (pro)action context?
    override fun enqueueMutation(
        mutation: () -> Unit,
    ) {
        enqueuedMutations.add(mutation)
    }

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
        reactionContext.enqueueProaction(
            proaction = proaction,
        )
    }
}
