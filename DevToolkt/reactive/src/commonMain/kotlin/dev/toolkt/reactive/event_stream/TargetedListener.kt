package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.managed_io.Transaction

/**
 * A helper object that's binding a target with an event lister that's
 * targeting it.
 */
data class TargetedListener<TargetT : Any, EventT>(
    val target: TargetT,
    val listener: TargetingListener<TargetT, EventT>,
) {
    fun captureTarget(): Listener<EventT> = object : UnconditionalListener<EventT>() {
        override fun handleUnconditionally(
            transaction: Transaction,
            event: EventT,
        ) {
            listener.handle(
                transaction = transaction,
                target = target,
                event = event
            )
        }
    }
}

/**
 * TODO: Nuke, but first nuke:
 *  - [SourcedListener]
 */
interface ISourcedListener<TargetT : Any> {
    fun bindTarget(
        target: TargetT,
    ): BoundListener
}

/**
 * TODO: Nuke (but modernize [take] / [takeUntilNull] first)
 */
data class SourcedListener<TargetT : Any, EventT>(
    val source: EventSource<EventT>,
    val listener: TargetingListener<TargetT, EventT>,
) : ISourcedListener<TargetT> {
    override fun bindTarget(
        target: TargetT,
    ): BoundListener = BoundTargetedListener(
        source = source,
        targetedListener = TargetedListener(
            target = target,
            listener = listener,
        ),
    )
}
