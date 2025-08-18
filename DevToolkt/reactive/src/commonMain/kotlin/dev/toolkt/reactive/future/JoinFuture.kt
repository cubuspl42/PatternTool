package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class JoinFuture<ValueT>(
    future: Future<Future<ValueT>>,
) : ProperFuture<ValueT>() {
    override val onResult: EventStream<ValueT> = future.state.divertOf { outerStateNow: State<Future<ValueT>> ->
        when (outerStateNow) {
            is Fulfilled<Future<ValueT>> -> outerStateNow.result.onResult

            Pending -> EventStream.Never
        }
    }

    override val state: Cell<State<ValueT>> = future.state.switchOf { outerStateNow: State<Future<ValueT>> ->
        when (outerStateNow) {
            is Fulfilled<Future<ValueT>> -> outerStateNow.result.state

            Pending -> Cell.of(Pending)
        }
    }
}
