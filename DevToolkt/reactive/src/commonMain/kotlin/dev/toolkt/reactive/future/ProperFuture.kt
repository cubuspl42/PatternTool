package dev.toolkt.reactive.future

import dev.toolkt.reactive.event_stream.EventStream

abstract class ProperFuture<out ResultT> : Future<ResultT>() {
    final override val currentStateUnmanaged: State<ResultT>
        get() = state.currentValueUnmanaged

    final override val onFulfilled: EventStream<Fulfilled<ResultT>>
        get() = onResult.singleUnmanaged().map { Fulfilled(result = it) }

    final override fun <TransformedResultT> map(
        transform: (ResultT) -> TransformedResultT,
    ): Future<TransformedResultT> = PlainFuture(
        state = state.map { stateNow ->
            stateNow.map(transform)
        },
    )
}
