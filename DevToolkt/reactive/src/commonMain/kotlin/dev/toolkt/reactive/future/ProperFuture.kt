package dev.toolkt.reactive.future

import dev.toolkt.reactive.event_stream.EventStream

abstract class ProperFuture<out V> : Future<V>() {
    final override val currentStateUnmanaged: State<V>
        get() = state.currentValueUnmanaged

    final override val onFulfilled: EventStream<Fulfilled<V>>
        get() = onResult.singleUnmanaged().map { Fulfilled(result = it) }

    final override fun <Vr> map(
        transform: (V) -> Vr,
    ): Future<Vr> = when (val foundState = currentStateUnmanaged) {
        is Fulfilled<V> -> of(
            constResult = transform(foundState.result),
        )

        Pending -> onResult.map(transform).next()
    }
}
