package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.future.Future

abstract class ProperEventStream<E> : EventStream<E>() {
    companion object {
        private var nextId = 0
    }

    protected var id = nextId++

    final override fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = MapEventStream(
        source = this,
        transform = transform,
    )

    final override fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = FilterEventStream(
        source = this,
        predicate = predicate,
    )

    override fun <Er : Any> mapNotNull(
        transform: (E) -> Er?,
    ): EventStream<Er> = MapNotNullEventStream(
        source = this,
        transform = transform,
    )

    final override fun take(
        count: Int,
    ): EventStream<E> {
        require(count >= 0)

        return when (count) {
            0 -> NeverEventStream

            else -> TakeEventStream(
                source = this,
                totalCount = count,
            )
        }
    }

    final override fun single(): EventStream<E> = SingleEventStream(source = this)

    final override fun next(): Future<E> = NextFuture(source = this)

    final override fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription = listenWeak(
        target = target,
        listener = forward,
    )

    override fun forEach(
        effect: (E) -> Unit,
    ) {
        listen(
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    effect(event)
                }
            },
        )
    }

    protected fun <EventT2> StrongEventSource<EventT2>.listen(
        listener: ListenerFn<EventT2>,
    ): Subscription = listenInDependent(
        dependentId = id,
        listener = listener,
    )
}
