package dev.toolkt.reactive.managed_io

interface ActionContext : MomentContext {
    fun enqueueMutation(
        mutate: () -> Unit,
    )
}

class Transaction private constructor() : ActionContext {
    companion object {
        fun <ResultT> execute(
            block: context(ActionContext) () -> ResultT,
        ): ResultT = with(Transaction()) {
            val result = block()

            finish()

            return@with result
        }
    }

    private val enqueuedReactions = mutableListOf<context(ActionContext) () -> Unit>()

    private val enqueuedMutations = mutableListOf<() -> Unit>()

    fun finish() {
        enqueuedMutations.forEach { mutate ->
            mutate()
        }
    }

    override val transaction: Transaction
        get() = this

    override fun enqueueMutation(mutate: () -> Unit) {
        enqueuedMutations.add(mutate)
    }
}

object Actions {
    fun <ResultT> external(
        block: context(ActionContext) () -> ResultT,
    ): ResultT = Transaction.execute(block = block)

    context(actionContext: ActionContext) fun defer(
        action: context(ActionContext) () -> Unit,
    ) {
//        reactionContext.enqueueAction(
//            action = action,
//        )
    }
}
