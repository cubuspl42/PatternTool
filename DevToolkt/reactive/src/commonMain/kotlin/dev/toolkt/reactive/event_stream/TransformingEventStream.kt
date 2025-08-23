package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction

internal abstract class TransformingEventStream<E, Er>(
    private val source: EventStream<E>,
) : PassiveEventStream<Er>() {
    final override fun observe(): Subscription = source.listen(
        object : UnconditionalListener<E>() {
            override val dependentId = id

            override fun handleUnconditionally(
                transaction: Transaction,
                event: E,
            ) {
                transformEvent(
                    transaction = transaction,
                    event = event,
                )
            }
        },
    )

    protected abstract fun transformEvent(
        transaction: Transaction,
        event: E,
    )
}
