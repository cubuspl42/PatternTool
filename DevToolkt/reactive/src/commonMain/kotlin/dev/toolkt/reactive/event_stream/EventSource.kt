package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.WeakEventSource.TargetedListener

interface WeakEventSource<out EventT> {
    /**
     * A helper object that's binding a target with an event lister that's
     * targeting it.
     */
    data class TargetedListener<TargetT, EventT>(
        val target: TargetT,
        val listener: TargetingListener<TargetT, EventT>,
    )

    fun <TargetT : Any> listenWeak(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Subscription
}

interface StrongEventSource<out EventT> {
    fun listen(
        listener: Listener<EventT>,
    ): Subscription
}

interface EventSource<out EventT> : WeakEventSource<EventT>, StrongEventSource<EventT>

fun <TargetT : Any, EventT> EventSource<EventT>.listenWeak(
    targetedWeakListener: TargetedListener<TargetT, EventT>,
): Subscription = listenWeak(
    target = targetedWeakListener.target,
    listener = targetedWeakListener.listener,
)

