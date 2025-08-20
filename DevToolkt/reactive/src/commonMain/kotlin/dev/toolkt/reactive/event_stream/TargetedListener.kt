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

