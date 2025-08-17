package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.Transaction

internal class TakeUntilNullStream<EventT : Any>(
    private val source: EventStream<EventT?>,
) : StatefulEventStream<TakeUntilNullStream<EventT>, EventT>() {
    override fun bind() = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<TakeUntilNullStream<EventT>, EventT?> {
            override fun handle(
                transaction: Transaction,
                target: TakeUntilNullStream<EventT>,
                event: EventT?,
            ) {
                when (event) {
                    null -> {
                        target.abort()
                    }

                    else -> {
                        target.notify(
                            transaction = transaction,
                            event = event
                        )
                    }
                }
            }
        },
    )

    init {
        init(target = this)
    }
}
