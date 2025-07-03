package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription

interface WeakEventSource<out EventT> {

    fun <TargetT : Any> listenWeak(
        target: TargetT,
        listener: TargetingListener<TargetT, EventT>,
    ): Subscription
}

fun <EventT, TargetT : Any> WeakEventSource<EventT>.listenWeak(
    target: TargetT,
    listener: TargetingListenerFn<TargetT, EventT>,
): Subscription = listenWeak(
    target,
    TargetingListener.wrap(listener),
)

interface StrongEventSource<out EventT> {
    fun listen(
        listener: Listener<EventT>,
    ): Subscription
}

fun <EventT> StrongEventSource<EventT>.listen(
    listener: ListenerFn<EventT>,
): Subscription = listen(
    object : Listener<EventT> {
        override fun handle(event: EventT) {
            listener(event)
        }
    },
)

interface EventSource<out EventT> : WeakEventSource<EventT>, StrongEventSource<EventT>

fun <TargetT : Any, EventT> EventSource<EventT>.listenWeak(
    targetedListener: TargetedListener<TargetT, EventT>,
): Subscription = listenWeak(
    target = targetedListener.target,
    listener = targetedListener.listener,
)
