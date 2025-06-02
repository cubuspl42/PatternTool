package dev.toolkt.reactive

sealed class SubscriptionStrength {
    data object Strong : SubscriptionStrength() {

    }

    data object Weak : SubscriptionStrength() {

    }
}



interface DependentNotifier<E> {
    fun notify(
        event: E,
    )
}

interface PropagationBehavior<E> {
    fun start(
        notifier: DependentNotifier<E>,
    ): Subscription
}

sealed interface EventStreamState<out E>


sealed interface FreshEventStreamState<out E> : EventStreamState<E>

// Active / Suspended
sealed class EngagedEventStreamState<out E> : EventStreamState<E>



class LinkedSubscription<out E>(
    private var linkedVertex: PropagationVertex<E>,
    private val eventHandler: EventHandler<E>,
    private val strength: SubscriptionStrength,
) : Subscription {
    fun relink(
        newLinkedVertex: PropagationVertex<@UnsafeVariance E>,
    ) {
        linkedVertex = newLinkedVertex
    }

    override fun cancel() {
        val removedSubscription = linkedVertex.unregister(
            eventHandler = eventHandler,
            strength = strength,
        )

        if (removedSubscription != this) {
            throw AssertionError("Unexpected removed subscription")
        }
    }
}

interface EventHandler<in E> {
    fun handleEvent(
        event: E,
    )

    fun handleStop()
}

data class SpecifiedEventHandler<in E>(
    val eventHandler: EventHandler<E>,
    val strength: SubscriptionStrength,
)
