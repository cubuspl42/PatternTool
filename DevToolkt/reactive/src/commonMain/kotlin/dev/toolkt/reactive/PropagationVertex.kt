package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformWeakMap

typealias OnLostSubscribersCallback = () -> Unit

class PropagationVertex<out E> private constructor(
    private val onLostSubscribers: OnLostSubscribersCallback,
) {
    companion object {
        fun <E> create(
            firstEventHandler: EventHandler<E>,
            firstEventHandlerStrength: SubscriptionStrength,
            onLostSubscribers: OnLostSubscribersCallback,
        ): Pair<PropagationVertex<E>, LinkedSubscription<E>> {
            val propagationVertex = PropagationVertex<E>(
                onLostSubscribers = onLostSubscribers,
            )

            val linkedSubscription = propagationVertex.init(
                eventHandler = firstEventHandler,
                strength = firstEventHandlerStrength,
            )

            return Pair(propagationVertex, linkedSubscription)
        }
    }

    private sealed class State {
        data object Uninitialized : State()
        data object Initialized : State()
        sealed class Destroyed : State()
        data object Suspended : Destroyed()
        data object Collapsed : Destroyed()
    }

    private var state: State = State.Uninitialized

    private val subscriptionByEventHandler = mutableMapOf<EventHandler<E>, LinkedSubscription<E>>()

    private val subscriptionByEventHandlerWeak = PlatformWeakMap<EventHandler<E>, LinkedSubscription<E>>()

    internal fun init(
        eventHandler: EventHandler<E>,
        strength: SubscriptionStrength,
    ): LinkedSubscription<E> {
        if (state != State.Uninitialized) {
            throw IllegalStateException()
        }

        TODO()
    }

    private val subscriptionCount: Int
        get() = 0

    fun register(
        eventHandler: EventHandler<E>,
        strength: SubscriptionStrength,
    ): LinkedSubscription<E> {


    }

    fun unregister(
        eventHandler: EventHandler<E>,
        strength: SubscriptionStrength,
    ) {

        if (subscriptionCount == 0) {
            // TODO: Suspend
        }
    }

    fun notify(
        event: E,
    ) {
        TODO()
    }
}
