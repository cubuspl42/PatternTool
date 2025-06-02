package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.EventStreamNg

typealias OnDeactivatedCallback = () -> Unit

data class ActiveEventStreamState<out E>(
    val upstreamSubscription: Subscription,
    val propagationVertex: PropagationVertex<E>,
    val onDeactivated: OnDeactivatedCallback,
) : EngagedEventStreamState<E>() {
    companion object {
        fun <E> build(
            upstreamSubscription: Subscription,
            firstEventHandler: EventHandler<E>,
            onDeactivated: OnDeactivatedCallback,
        ): Pair<ActiveEventStreamState<E>, LinkedSubscription<E>> {
            val (propagationVertex, downstreamSubscription) = PropagationVertex.create(
                firstEventHandler = firstEventHandler,
                firstEventHandlerStrength = null,
                onLostSubscribers = {

                },
            )

            val activeState: ActiveEventStreamState<E> = ActiveEventStreamState(
                upstreamSubscription = upstreamSubscription,
                propagationVertex = propagationVertex,
                onDeactivated = onDeactivated,
            )

            return Pair(
                activeState,
                downstreamSubscription,
            )
        }
    }

}

data object SuspendedEventStreamState : EngagedEventStreamState<Nothing>(), FreshEventStreamState<Nothing>

sealed class TerminalEventStreamState<E> : EventStreamState<E>

data class SucceededEventStreamState<E>(
    val successorEventStream: EventStreamNg<E>,
) : TerminalEventStreamState<E>()

data object DrainedEventStreamState : TerminalEventStreamState<Nothing>(), FreshEventStreamState<Nothing>

