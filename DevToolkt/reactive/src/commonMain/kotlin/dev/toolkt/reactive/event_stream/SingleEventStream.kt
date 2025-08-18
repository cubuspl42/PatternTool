package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.managed_io.Transaction

class SingleEventStream<EventT>(
    private val source: EventStream<EventT>,
) : StatefulEventStream<SingleEventStream<EventT>, EventT>() {
    private var wasEmitted = false

    override fun bind() = source.bind(
        // The targeting listener cannot capture a strong reference to the outer stream. It's very important and
        // extremely easy to miss.
        listener = object : TargetingListener<SingleEventStream<EventT>, EventT> {
            override fun handle(
                transaction: Transaction,
                target: SingleEventStream<EventT>,
                event: EventT,
            ): Listener.Conclusion {
                if (target.wasEmitted) {
                    // Abortion failed (?)
                    throw AssertionError("The single event was already emitted")
                }

                target.notify(
                    transaction = transaction,
                    event = event
                )

                target.wasEmitted = true

                target.abort()

                // FIXME
                return Listener.Conclusion.KeepListening
            }
        },
    )

    init {
        init(target = this)
    }
}
