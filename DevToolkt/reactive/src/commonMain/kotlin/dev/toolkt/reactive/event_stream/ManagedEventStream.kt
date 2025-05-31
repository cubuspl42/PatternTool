package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.ProperEventStream.SubscriptionSet.SubscriptionStrength

abstract class ManagedEventStream<E> : ProperEventStream<E>() {
    final override fun register(
        eventHandler: EventHandler<E>,
        strength: SubscriptionStrength,
    ): LinkedSubscription<@UnsafeVariance E>? = super.register(
        eventHandler = eventHandler,
        strength = strength,
    )

    final override fun unregister(
        eventHandler: EventHandler<E>,
        strength: SubscriptionStrength,
    ): LinkedSubscription<E> = super.unregister(
        eventHandler = eventHandler,
        strength = strength,
    )
}
