package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.managed_io.Transaction

internal class TakeUntilNullStream<EventT : Any>(
    private val source: EventStream<EventT?>,
) : StatefulEventStream<TakeUntilNullStream<EventT>, EventT>() {
    override fun bind() = source.bindSourced(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<TakeUntilNullStream<EventT>, EventT?> {
            override fun handle(
                transaction: Transaction,
                target: TakeUntilNullStream<EventT>,
                event: EventT?,
            ): Listener.Conclusion {
                when (event) {
                    null -> {
                        target.abort()

                        return Listener.Conclusion.StopListening
                    }

                    else -> {
                        target.notify(
                            transaction = transaction,
                            event = event
                        )

                        return Listener.Conclusion.KeepListening
                    }
                }
            }
        },
    )

    init {
        init(target = this)
    }
}
