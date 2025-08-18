package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class PlainFuture<out V>(
    override val state: Cell<State<V>>,
) : Future<V>() {

    override val currentStateUnmanaged: State<V>
        get() = state.currentValueUnmanaged

    override val onFulfilled: EventStream<Fulfilled<V>> = state.newValues.mapNotNull {
        it as? Fulfilled<V>
    }

    override val onResult: EventStream<V> = onFulfilled.map { it.result }

    override fun <Vr> map(
        transform: (V) -> Vr,
    ): Future<Vr> = PlainFuture(
        state.map { stateNow ->
            when (stateNow) {
                is Fulfilled<V> -> Fulfilled(
                    result = transform(stateNow.result),
                )

                Pending -> Pending
            }
        },
    )
}
