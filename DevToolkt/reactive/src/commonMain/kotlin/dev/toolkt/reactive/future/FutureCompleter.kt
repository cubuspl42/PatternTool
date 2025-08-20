package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.effect.ActionContext

class FutureCompleter<V> : ProperFuture<V>() {
    private val mutableState = MutableCell<State<V>>(Pending)

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
