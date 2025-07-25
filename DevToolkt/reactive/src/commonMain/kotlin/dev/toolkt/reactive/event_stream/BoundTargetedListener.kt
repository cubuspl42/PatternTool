package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.HybridSubscription.Companion.listenHybrid
import dev.toolkt.reactive.Subscription

data class BoundTargetedListener<TargetT : Any, EventT>(
    val source: EventSource<EventT>,
    val targetedListener: TargetedListener<TargetT, EventT>,
) : BoundListener {
    override fun listen(): Subscription = source.listen(
        listener = targetedListener.captureTarget(),
    )

    override fun listenWeak(): Subscription = source.listenWeak(
        targetedListener = targetedListener,
    )

    override fun listenHybrid(): HybridSubscription = source.listenHybrid(
        target = targetedListener.target,
        targetingListener = targetedListener.listener,
    )
}
