package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription

fun <TargetT : Any, EventT> EventSource<EventT>.pinWeak(
    target: TargetT,
): Subscription = listenWeak(
    target = target,
    listener = TargetingListener.Noop,
)

fun <TargetT : Any, EventT> EventSource<EventT>.listenWeak(
    target: TargetT,
    listener: TargetingListenerFn<TargetT, EventT>,
): Subscription = listenWeak(
    target = target,
    listener = TargetingListener.wrap(listener),
)

private val finalizationRegistry = PlatformFinalizationRegistry()

fun <TargetT : Any, EventT> EventSource<EventT>.listenWeak(
    target: TargetT,
    listener: TargetingListener<TargetT, EventT>,
): Subscription {
    val targetWeakRef = PlatformWeakReference(target)

    val innerSubscription = this.listen(
        listener = object : Listener<EventT> {
            override fun handle(event: EventT) {
                // If the target was collected, we assume that this listener
                // will soon be removed. For now, let's just ignore the event.
                val target = targetWeakRef.get() ?: return

                listener.handle(
                    target = target,
                    event = event,
                )
            }
        },
    )

    val cleanable = finalizationRegistry.register(
        target = target,
    ) {
        innerSubscription.cancel()
    }

    return object : Subscription {
        override fun cancel() {
            cleanable.clean()
        }
    }
}

interface StrongEventSource<out EventT> {
    fun listen(
        listener: Listener<EventT>,
    ): Subscription
}

fun <EventT> StrongEventSource<EventT>.listenInDependent(
    dependentId: Int,
    listener: ListenerFn<EventT>,
): Subscription = listen(
    object : Listener<EventT> {
        override val dependentId: Int = dependentId

        override fun handle(event: EventT) {
            listener(event)
        }
    },
)

fun <EventT> StrongEventSource<EventT>.listenExternally(
    listener: ListenerFn<EventT>,
): Subscription = listen(
    object : Listener<EventT> {
        override fun handle(event: EventT) {
            listener(event)
        }
    },
)

interface EventSource<out EventT> : StrongEventSource<EventT>

fun <TargetT : Any, EventT> EventSource<EventT>.listenWeak(
    targetedListener: TargetedListener<TargetT, EventT>,
): Subscription = this@listenWeak.listenWeak(
    target = targetedListener.target,
    listener = targetedListener.listener,
)
