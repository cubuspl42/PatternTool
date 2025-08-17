package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.managed_io.Transaction

abstract class TransformingEventStream<E, Er>(
    private val source: EventStream<E>,
) : DependentEventStream<Er>() {
    final override fun observe(): Subscription = source.listen(
        object : Listener<E> {
            override val dependentId = id

            override fun handle(
                transaction: Transaction,
                event: E,
            ) {
                TODO("Not yet implemented")
            }
        },
    )

    protected abstract fun transformEvent(
        transaction: Transaction,
        event: E,
    )
}
