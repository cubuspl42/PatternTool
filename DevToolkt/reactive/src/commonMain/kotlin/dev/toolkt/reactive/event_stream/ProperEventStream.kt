package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.future.Future.Pending
import dev.toolkt.reactive.future.PlainFuture
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction

abstract class ProperEventStream<E> : EventStream<E>() {
    companion object {
        private var nextId = 0
    }

    internal var id = nextId++

    final override fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = MapEventStream(
        source = this,
        transform = transform,
    )

    final override fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = FilterEventStream.construct(
        source = this,
        predicate = predicate,
    )

    override fun <Er : Any> mapNotNull(
        transform: (E) -> Er?,
    ): EventStream<Er> = MapNotNullEventStream(
        source = this,
        transform = transform,
    )

    context(momentContext: MomentContext) final override fun take(
        count: Int,
    ): EventStream<E> {
        require(count >= 0)

        return when (count) {
            0 -> NeverEventStream

            else -> TakeEventStream.construct(
                source = this,
                count = count,
            )
        }
    }

    context(momentContext: MomentContext) final override fun next(): Future<E> = PlainFuture(
        state = single().map {
            Future.Fulfilled(it)
        }.hold(Pending),
    )

    final override fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription = listenWeak(
        target = target,
        listener = forward,
    )

    override fun forEachUnmanaged(
        effect: (E) -> Unit,
    ) {
        listen(
            listener = object : UnconditionalListener<E>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: E,
                ) {
                    effect(event)
                }
            },
        )
    }

    protected fun <EventT2> EventStream<EventT2>.listen(
        listener: ListenerFn<EventT2>,
    ): Subscription = listenInDependent(
        dependentId = id,
        listener = listener,
    )
}
