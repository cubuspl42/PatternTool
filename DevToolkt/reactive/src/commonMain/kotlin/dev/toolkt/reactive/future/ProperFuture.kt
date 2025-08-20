package dev.toolkt.reactive.future

abstract class ProperFuture<out ResultT> : Future<ResultT>() {
    final override val currentStateUnmanaged: State<ResultT>
        get() = state.currentValueUnmanaged

    final override fun <TransformedResultT> map(
        transform: (ResultT) -> TransformedResultT,
    ): Future<TransformedResultT> = PlainFuture(
        state = state.map { stateNow ->
            stateNow.map(transform)
        },
    )
}
