package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.event_stream.EventStream

class FutureCompleter<V> : ProperFuture<V>() {
    private val mutableState = MutableCell<State<V>>(Pending)

    fun complete(
        result: V,
    ) {
        when (mutableState.currentValue) {
            is Fulfilled<V> -> throw IllegalStateException("The future is already fulfilled")
            Pending -> {
                mutableState.setUnmanaged(Fulfilled(result = result))
            }
        }
    }

    override val onResult: EventStream<V>
        get() = mutableState.newValues.mapNotNull {
            (it as? Fulfilled<V>)?.result
        }

    override val state: Cell<State<V>>
        get() = mutableState
}
