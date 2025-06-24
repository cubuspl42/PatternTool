package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.WeakEventSource.TargetedWeakListener

interface WeakEventSource<out E> {
    data class TargetedWeakListener<T, E>(
        val target: T,
        val listener: WeakListener<T, E>,
    )

    fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription
}

interface StrongEventSource<out E> {
    fun listen(
        listener: Listener<E>,
    ): Subscription
}

interface EventSource<out E> : WeakEventSource<E>, StrongEventSource<E>

fun <T : Any, E> EventSource<E>.listenWeak(
    targetedWeakListener: TargetedWeakListener<T, E>,
): Subscription = listenWeak(
    target = targetedWeakListener.target,
    listener = targetedWeakListener.listener,
)

