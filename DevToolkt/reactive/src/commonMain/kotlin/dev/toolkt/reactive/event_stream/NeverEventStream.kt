package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.managed_io.MomentContext

object NeverEventStream : EventStream<Nothing>() {
    override fun <Er> map(
        transform: (Nothing) -> Er,
    ): EventStream<Er> = NeverEventStream

    override fun <Er : Any> mapNotNull(
        transform: (Nothing) -> Er?,
    ): EventStream<Er> = NeverEventStream

    override fun filter(
        predicate: (Nothing) -> Boolean,
    ): EventStream<Nothing> = NeverEventStream

    override fun take(count: Int): EventStream<Nothing> {
        require(count >= 0)
        return NeverEventStream
    }

    context(momentContext: MomentContext) override fun next(): Future<Nothing> = Future.Hang

    override fun forEachUnmanaged(
        effect: (Nothing) -> Unit,
    ) {
    }

    override fun <T : Any> pipe(
        target: T,
        forward: (T, Nothing) -> Unit,
    ): Subscription = Subscription.Noop

    override fun listen(
        listener: Listener<Nothing>,
    ): Subscription = Subscription.Noop
}
