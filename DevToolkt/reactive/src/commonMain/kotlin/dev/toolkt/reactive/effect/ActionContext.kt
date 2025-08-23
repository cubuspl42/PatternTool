package dev.toolkt.reactive.effect

interface ActionContext : MomentContext {
    /**
     * Currently this includes both proper state mutation (e.g. MutableCell internal state) and signaling network
     * mutations (e.g. adding / removing stream listeners)
     */
    fun enqueueMutation(
        mutate: () -> Unit,
    )
}

class Transaction private constructor(
    private val followupQueue: FollowupQueue,
) : ActionContext {
    private enum class State {
        Ongoing,
        Finished,
    }

    private class FollowupQueue {
        private val queue = ArrayDeque<(Transaction) -> Unit>()

        fun enqueueFollowup(
            followupBlock: (Transaction) -> Unit,
        ) {
            queue.addLast(followupBlock)
        }

        fun dequeueFollowup(): ((Transaction) -> Unit)? = queue.removeFirstOrNull()
    }

    companion object {
        internal fun <ResultT> executeAll(
            firstBlock: (Transaction) -> ResultT,
        ): ResultT {
            val followupQueue = FollowupQueue()

            return Transaction.executeSingle(
                followupQueue = followupQueue,
                block = firstBlock,
            ).also { result ->
                while (true) {
                    val followupBlock = followupQueue.dequeueFollowup() ?: break

                    Transaction.executeSingle(
                        followupQueue = followupQueue,
                        block = followupBlock,
                    )
                }
            }
        }

        private fun <ResultT> executeSingle(
            followupQueue: FollowupQueue,
            block: (Transaction) -> ResultT,
        ): ResultT = Transaction(
            followupQueue = followupQueue,
        ).let {
            val result = block(it)

            it.finish()

            result
        }
    }

    private var state = State.Ongoing

    private val enqueuedMutations = mutableListOf<() -> Unit>()

    fun finish() {
        if (state == State.Finished) {
            throw IllegalStateException("Transaction already finished")
        }

        enqueuedMutations.forEach { mutate ->
            mutate()
        }

        state = State.Finished
    }

    override val transaction: Transaction
        get() = this

    override fun enqueueMutation(mutate: () -> Unit) {
        if (state == State.Finished) {
            throw IllegalStateException("Transaction already finished")
        }

        enqueuedMutations.add(mutate)
    }

    /**
     * Enqueues a follow-up operation to be executed in a new transaction right
     * after the current transaction.
     *
     * @param followup The follow-up operation to be executed in the followup transaction's propagation phase
     */
    fun enqueueFollowup(
        followup: (Transaction) -> Unit,
    ) {
        if (state == State.Finished) {
            throw IllegalStateException("Transaction already finished")
        }

        followupQueue.enqueueFollowup(
            followupBlock = followup,
        )
    }
}

object Actions {
    fun <ResultT> external(
        block: context(ActionContext) () -> ResultT,
    ): ResultT = Transaction.executeAll(
        firstBlock = { transaction ->
            return@executeAll with(transaction) {
                block()
            }
        },
    )

    context(momentContext: MomentContext) fun <ResultT> local(
        block: context(ActionContext) () -> ResultT,
    ): ResultT = with(momentContext.transaction) {
        block()
    }

    context(actionContext: ActionContext) fun mutate(
        mutate: () -> Unit,
    ) {
        actionContext.enqueueMutation(
            mutate = mutate,
        )
    }
}
