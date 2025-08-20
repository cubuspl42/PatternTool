package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener.Conclusion
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction

internal class SingleEventStream<EventT> private constructor() : StatefulEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> construct(
            source: EventStream<EventT>,
        ): SingleEventStream<EventT> = SingleEventStream<EventT>().apply {
            momentContext.transaction.enqueueMutation {
                source.listenWeak(
                    target = this@apply,
                    listener = object : TargetingListener<SingleEventStream<EventT>, EventT> {
                        override fun handle(
                            transaction: Transaction,
                            target: SingleEventStream<EventT>,
                            event: EventT,
                        ): Conclusion {
                            val self = target

                            if (self.wasEmitted) {
                                // Abortion failed (?)
                                throw AssertionError("The single event was already emitted")
                            }

                            self.notify(
                                transaction = transaction,
                                event = event,
                            )

                            // Thought: How does this handle diamond-shaped event stream chains?
                            self.wasEmitted = true

                            self.abort()

                            return Conclusion.StopListening
                        }
                    },
                )
            }
        }
    }

    private var wasEmitted = false
}

