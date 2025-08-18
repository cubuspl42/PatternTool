package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener.Conclusion
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction

class SingleEventStreamNg<EventT> private constructor() : ManagedEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> construct(
            source: EventStream<EventT>,
        ): SingleEventStreamNg<EventT> = SingleEventStreamNg<EventT>().apply {
            momentContext.transaction.enqueueMutation {
                source.listenWeak(
                    target = this@apply,
                    listener = object : TargetingListener<SingleEventStreamNg<EventT>, EventT> {
                        override fun handle(
                            transaction: Transaction,
                            target: SingleEventStreamNg<EventT>,
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

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
