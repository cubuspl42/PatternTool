package dev.toolkt.reactive.event_stream.vertex

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream
import kotlin.jvm.JvmInline

abstract class TransformingEventStreamVertex<EventT, TransformedEventT>(
    private val source: EventStream<EventT>,
) : PassiveEventStreamVertex<TransformedEventT>() {
    /**
     * A wrapper for the transformed event, allowing nullable [TransformedEventT]
     */
    @JvmInline
    value class EventTransformation<TransformedEventT>(
        val transformedEvent: TransformedEventT,
    )

    override fun observe(): Subscription = source.listen(
        object : UnconditionalListener<EventT>() {
            override fun handleUnconditionally(
                transaction: Transaction,
                event: EventT,
            ) {
                transformEvent(
                    transaction = transaction,
                    event = event,
                )?.let {
                    notify(
                        transaction = transaction,
                        event = it.transformedEvent,
                    )
                }
            }
        },
    )

    protected abstract fun transformEvent(
        transaction: Transaction,
        event: EventT,
    ): EventTransformation<TransformedEventT>?
}
