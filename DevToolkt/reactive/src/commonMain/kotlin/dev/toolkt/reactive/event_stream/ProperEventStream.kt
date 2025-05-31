package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformWeakMap
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Future

abstract class ProperEventStream<E> : EventStream<E>() {
    data class SubscriptionSet<E>(
        private val subscriptionByDependentStrong: MutableMap<EventHandler<E>, LinkedSubscription<E>>,
        private val subscriptionByDependentWeak: PlatformWeakMap<EventHandler<E>, LinkedSubscription<E>>,
    ) {
        constructor() : this(
            subscriptionByDependentStrong = mutableMapOf(),
            subscriptionByDependentWeak = PlatformWeakMap(),
        )

        sealed class SubscriptionStrength {
            data object Strong : SubscriptionStrength() {
                override fun <E> extractMutableSet(
                    subscriptionSet: SubscriptionSet<E>,
                ): MutableMap<EventHandler<E>, LinkedSubscription<E>> = subscriptionSet.subscriptionByDependentStrong
            }

            data object Weak : SubscriptionStrength() {
                override fun <E> extractMutableSet(
                    subscriptionSet: SubscriptionSet<E>,
                ): MutableMap<EventHandler<E>, LinkedSubscription<E>> = subscriptionSet.subscriptionByDependentWeak
            }

            abstract fun <E> extractMutableSet(subscriptionSet: SubscriptionSet<E>): MutableMap<EventHandler<E>, LinkedSubscription<E>>
        }

        val dependents: Set<EventHandler<E>>
            get() = this.subscriptionByDependentWeak.keys + this.subscriptionByDependentStrong.keys

        fun add(
            dependent: EventHandler<E>,
            subscription: LinkedSubscription<E>,
            strength: SubscriptionStrength,
        ): LinkedSubscription<E>? {
            val subscriptionByDependent = strength.extractMutableSet(subscriptionSet = this)

            return subscriptionByDependent.put(dependent, subscription)
        }

        fun remove(
            dependent: EventHandler<E>,
            strength: SubscriptionStrength,
        ): LinkedSubscription<E>? {
            val subscriptionByDependent = strength.extractMutableSet(subscriptionSet = this)

            return subscriptionByDependent.remove(dependent)
        }

        fun addAll(
            other: SubscriptionSet<E>,
        ) {
            subscriptionByDependentStrong.putAll(other.subscriptionByDependentStrong)
            subscriptionByDependentWeak.putAll(other.subscriptionByDependentWeak)
        }

        fun forEach(
            action: (Map.Entry<EventHandler<E>, LinkedSubscription<E>>) -> Unit,
        ) {
            subscriptionByDependentStrong.forEach(action)
            subscriptionByDependentWeak.forEach(action)
        }

        val size: Int
            get() = subscriptionByDependentStrong.size + subscriptionByDependentWeak.size
    }

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

    override fun take(
        count: Int,
    ): EventStream<E> {
        require(count >= 0)

        return when (count) {
            0 -> NeverEventStream

            else -> TakeEventStream(
                source = this,
                count = count,
            )
        }
    }

    final override fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription = TODO()

    private var subscriptionSet: SubscriptionSet<E>? = SubscriptionSet()

    override fun register(
        eventHandler: EventHandler<E>,
        strength: SubscriptionSet.SubscriptionStrength,
    ): LinkedSubscription<E>? {
        val subscriptionSet = this.subscriptionSet ?: return null

        val linkedSubscription = LinkedSubscription(
            linkedEventSource = this,
            eventHandler = eventHandler,
            strength = strength,
        )

        val previousSubscription = subscriptionSet.add(
            dependent = eventHandler,
            subscription = linkedSubscription,
            strength = strength,
        )

        if (previousSubscription != null) {
            throw IllegalStateException("Dependent is already registered")
        }

        if (subscriptionSet.size == 1) {
            onResumed()
        }

        return linkedSubscription
    }

    override fun unregister(
        eventHandler: EventHandler<E>,
        strength: SubscriptionSet.SubscriptionStrength,
    ): LinkedSubscription<E> {
        val subscriptionSet =
            this.subscriptionSet ?: throw IllegalArgumentException("Cannot unsubscribe from a stopped stream")

        val removedSubscription = subscriptionSet.remove(
            dependent = eventHandler,
            strength = strength,
        ) ?: throw IllegalStateException("The subscription for the dependent does not exist")

        if (subscriptionSet.size == 0) {
            onPaused()
        }

        return removedSubscription
    }

    fun internalize(
        other: ProperEventStream<E>,
    ) {
        val subscriptionSet =
            this.subscriptionSet ?: throw IllegalArgumentException("Cannot internalize into a stopped stream")

        val otherSubscriptionSet =
            other.subscriptionSet ?: throw IllegalArgumentException("Cannot internalize a stopped stream")

        other.subscriptionSet = null

        subscriptionSet.addAll(otherSubscriptionSet)

        otherSubscriptionSet.forEach { (_, subscription) ->
            subscription.relink(newEventSource = this)
        }
    }

    final override fun next(): Future<E> {
        TODO("Not yet implemented")
    }

    protected fun notify(
        event: E,
    ) {
        val subscriptionSet =
            this.subscriptionSet ?: throw IllegalStateException("Cannot notify dependents of a stopped stream")

        subscriptionSet.dependents.forEach { dependent ->
            dependent.handleEvent(
                source = this,
                event = event,
            )
        }

        if (subscriptionSet.size == 0) {
            onPaused()
        }
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}
