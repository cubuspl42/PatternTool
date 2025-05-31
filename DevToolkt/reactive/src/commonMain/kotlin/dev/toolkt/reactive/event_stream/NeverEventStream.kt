package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Future

object NeverEventStream : EventStream<Nothing>() {
    override fun <Er> map(
        transform: (Nothing) -> Er,
    ): EventStream<Er> = NeverEventStream

    override fun filter(
        predicate: (Nothing) -> Boolean,
    ): EventStream<Nothing> = NeverEventStream

    override fun take(count: Int): EventStream<Nothing> {
        require(count >= 0)
        return NeverEventStream
    }

    override fun next(): Future<Nothing> = Future.Hang

    override fun <T : Any> pipe(
        target: T,
        forward: (T, Nothing) -> Unit,
    ): Subscription = Subscription.Noop

    override val successorEventStream: EventStream<Nothing>?
        get() = null

    override fun register(
        eventHandler: EventHandler<Nothing>,
        strength: ProperEventStream.SubscriptionSet.SubscriptionStrength
    ): LinkedSubscription<Nothing>? = null

    override fun unregister(
        eventHandler: EventHandler<Nothing>,
        strength: ProperEventStream.SubscriptionSet.SubscriptionStrength
    ): LinkedSubscription<Nothing> {
        throw UnsupportedOperationException()
    }
}
