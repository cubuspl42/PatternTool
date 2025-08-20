package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell

class PlainFuture<out V>(
    override val state: Cell<State<V>>,
) : Future<V>() {

    override val currentStateUnmanaged: State<V>
        get() = state.currentValueUnmanaged

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
