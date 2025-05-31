package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.ProperEventStream.SubscriptionSet

interface EventSource<out E> {
    data class TargetedWeakListener<T, E>(
        val target: T,
        val listener: WeakListener<T, E>,
    )

    fun register(
        eventHandler: EventHandler<E>,
        strength: SubscriptionSet.SubscriptionStrength,
    ): LinkedSubscription<E>?

    fun unregister(
        eventHandler: EventHandler<E>,
        strength: SubscriptionSet.SubscriptionStrength,
    ): LinkedSubscription<E>
}

fun <E> EventSource<E>.subscribeWeak(
    eventHandler: EventHandler<E>,
): LinkedSubscription<E>? = register(
    eventHandler = eventHandler,
    strength = SubscriptionSet.SubscriptionStrength.Weak,
)

fun <E> EventSource<E>.subscribeStrong(
    eventHandler: EventHandler<E>,
): LinkedSubscription<E>? = register(
    eventHandler = eventHandler,
    strength = SubscriptionSet.SubscriptionStrength.Strong,
)


fun <E> EventSource<E>.unsubscribeWeak(
    dependent: EventHandler<E>,
): LinkedSubscription<E> = unregister(
    eventHandler = dependent,
    strength = SubscriptionSet.SubscriptionStrength.Weak,
)

fun <E> EventSource<E>.unsubscribeStrong(
    dependent: EventHandler<E>,
): LinkedSubscription<E> = unregister(
    eventHandler = dependent,
    strength = SubscriptionSet.SubscriptionStrength.Strong,
)

class LinkedSubscription<out E>(
    private var linkedEventSource: EventSource<E>,
    private val eventHandler: EventHandler<E>,
    private val strength: SubscriptionSet.SubscriptionStrength,
) : Subscription {
    fun relink(
        newEventSource: EventSource<@UnsafeVariance E>,
    ) {
        linkedEventSource = newEventSource
    }

    override fun cancel(): EventSource<E> {
        val linkedEventSource = this.linkedEventSource

        val removedSubscription = linkedEventSource.unregister(
            eventHandler = eventHandler,
            strength = strength,
        )

        if (removedSubscription != this) {
            throw AssertionError("Unexpected removed subscription")
        }

        return linkedEventSource
    }
}

interface EventHandler<in E> {
    fun handleEvent(
        source: EventSource<E>,
        event: E,
    )

    fun handleStop(
        source: EventSource<E>,
    )
}