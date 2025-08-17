package dev.toolkt.reactive.managed_io

interface ActionContext : MomentContext

private class ActionContextImpl : ActionContext {
    companion object {
        fun <ResultT> execute(
            block: context(ActionContext) () -> ResultT,
        ): ResultT {
            TODO()
        }
    }

    private val enqueuedReactions = mutableListOf<context(ActionContext) () -> Unit>()

    private val enqueuedMutations = mutableListOf<() -> Unit>()

    fun finish() {
        enqueuedReactions.forEach { action ->
            TODO()
        }
    }
}

object Actions {
    fun <ResultT> external(
        block: context(ActionContext) () -> ResultT,
    ): ResultT = with(ActionContextImpl()) {
        block()
    }

    context(actionContext: ActionContext) fun defer(
        action: context(ActionContext) () -> Unit,
    ) {
//        reactionContext.enqueueAction(
//            action = action,
//        )
    }
}
