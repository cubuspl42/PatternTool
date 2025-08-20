package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext

class FutureCompleter<V> private constructor(
    private val mutableState: MutableCell<Future.State<V>>,
) : ProperFuture<V>() {
    companion object {
        context(momentContext: MomentContext) fun <ResultT> create(): FutureCompleter<ResultT> = FutureCompleter(
            mutableState = MutableCell.create<State<ResultT>>(
                initialValue = Pending,
            ),
        )
    }

    val hasListeners: Boolean
        get() = mutableState.hasListeners

    context(actionContext: ActionContext) fun complete(
        result: V,
    ) {
        when (mutableState.sample()) {
            is Fulfilled<V> -> throw IllegalStateException("The future is already fulfilled")

            Pending -> {
                mutableState.set(
                    Fulfilled(result = result),
                )
            }
        }
    }

    override val state: Cell<State<V>>
        get() = mutableState
}
