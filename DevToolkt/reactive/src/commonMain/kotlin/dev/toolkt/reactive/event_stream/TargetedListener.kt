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
    fun bindSource(
        source: EventSource<EventT>,
    ): BoundTargetedListener<TargetT, EventT> = BoundTargetedListener(
        source = source,
        targetedListener = this,
    )

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

interface ISourcedListener<TargetT : Any> {
    fun bindTarget(
        target: TargetT,
    ): BoundListener
}

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
