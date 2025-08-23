package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Listener.Conclusion
import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.vertex.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertex.subscribeNow

internal fun <TargetT : Any, EventT> EventStream<EventT>.pinWeak(
    target: TargetT,
): Subscription = listenWeak(
    target = target,
    listener = TargetingListener.Noop,
)

internal fun <TargetT : Any, EventT> EventStreamVertex<EventT>.pinWeak(
    target: TargetT,
): Subscription = subscribeNowWeak(
    target = target,
    listener = TargetingListener.Noop,
)

internal fun <TargetT : Any, EventT> EventStream<EventT>.listenWeak(
    target: TargetT,
    listener: TargetingListenerFn<TargetT, EventT>,
): Subscription = listenWeak(
    target = target,
    listener = TargetingListener.wrap(listener),
)

private val finalizationRegistry = PlatformFinalizationRegistry()

internal fun <TargetT : Any, EventT> EventStream<EventT>.listenWeak(
    target: TargetT,
    listener: TargetingListener<TargetT, EventT>,
): Subscription {
    val targetWeakRef = PlatformWeakReference(target)

    val innerSubscription = this.listen(
        listener = object : Listener<EventT> {
            override fun handle(
                transaction: Transaction,
                event: EventT,
            ): Conclusion {
                val target = targetWeakRef.get() ?: return Conclusion.StopListening

                return listener.handle(
                    transaction = transaction,
                    target = target,
                    event = event,
                )
            }
        },
    )

    val targetHashCode = target.hashCode()
    println("Registering target #$targetHashCode...")

    val cleanable = finalizationRegistry.register(
        target = target,
    ) {
        println("Cleaning target #$targetHashCode")
        innerSubscription.cancel()
    }

    return object : Subscription {
        override fun cancel() {
            cleanable.clean()
        }
    }
}

// TODO: De-duplicate
internal fun <TargetT : Any, EventT> EventStreamVertex<EventT>.subscribeNowWeak(
    target: TargetT,
    listener: TargetingListener<TargetT, EventT>,
): Subscription {
    val targetWeakRef = PlatformWeakReference(target)

    val innerSubscription = this.subscribeNow(
        listener = object : Listener<EventT> {
            override fun handle(
                transaction: Transaction,
                event: EventT,
            ): Conclusion {
                val target = targetWeakRef.get() ?: return Conclusion.StopListening

                return listener.handle(
                    transaction = transaction,
                    target = target,
                    event = event,
                )
            }
        },
    )

    val targetHashCode = target.hashCode()
    println("Registering target #$targetHashCode...")

    val cleanable = finalizationRegistry.register(
        target = target,
    ) {
        println("Cleaning target #$targetHashCode")
        innerSubscription.cancel()
    }

    return object : Subscription {
        override fun cancel() {
            cleanable.clean()
        }
    }
}

fun <EventT> EventStream<EventT>.listenInDependent(
    dependent: ProperEventStream<*>,
    listener: ListenerFn<EventT>,
): Subscription = listenInDependent(
    dependentId = dependent.id,
    listener = listener,
)


// TODO: Nuke?
fun <EventT> EventStream<EventT>.listenInDependent(
    dependentId: Int,
    listener: ListenerFn<EventT>,
): Subscription = listen(
    object : UnconditionalListener<EventT>() {
        override val dependentId: Int = dependentId

        override fun handleUnconditionally(
            transaction: Transaction,
            event: EventT,
        ) {
            listener(event)
        }
    },
)


// TODO: Nuke, or at least move to tests?
fun <EventT> EventStream<EventT>.listenExternally(
    listener: ListenerFn<EventT>,
): Subscription = listen(
    object : UnconditionalListener<EventT>() {
        override fun handleUnconditionally(
            transaction: Transaction,
            event: EventT,
        ) {
            listener(event)
        }
    },
)

internal fun <TargetT : Any, EventT> EventStream<EventT>.listenWeak(
    targetedListener: TargetedListener<TargetT, EventT>,
): Subscription = this@listenWeak.listenWeak(
    target = targetedListener.target,
    listener = targetedListener.listener,
)
