package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener

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

data class SourcedListener<TargetT : Any, EventT>(
    val source: EventSource<EventT>,
    val listener: TargetingListener<TargetT, EventT>,
) {
    fun bindTarget(
        target: TargetT,
    ): BoundTargetedListener<TargetT, EventT> = BoundTargetedListener(
        source = source,
        targetedListener = TargetedListener(
            target = target,
            listener = listener,
        ),
    )
}
