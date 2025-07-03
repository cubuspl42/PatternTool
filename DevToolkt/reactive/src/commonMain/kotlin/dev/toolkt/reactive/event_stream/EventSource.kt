package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.WeakEventSource.TargetedListener

interface WeakEventSource<out EventT> {
    /**
     * A helper object that's binding a target with an event lister that's
     * targeting it.
     */
    data class TargetedListener<TargetT : Any, EventT>(
        val target: TargetT,
        val listener: TargetingListener<TargetT, EventT>,
    ) {
        fun bindSource(
            source: EventSource<EventT>,
        ): BoundTargetedListener<TargetT, EventT> = BoundTargetedListener(
            source = source,
            targetedListener = this,
        )

        fun captureTarget(): Listener<EventT> = object : Listener<EventT> {
            override fun handle(event: EventT) {
                listener.handle(target, event)
            }
        }
    }

    data class BoundTargetedListener<TargetT : Any, EventT>(
        val source: EventSource<EventT>,
        val targetedListener: TargetedListener<TargetT, EventT>,
    ) {
        fun listen(): Subscription = source.listen(
            listener = targetedListener.captureTarget(),
        )

        fun listenWeak(): Subscription = source.listenWeak(
            targetedListener = targetedListener,
        )
    }

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
