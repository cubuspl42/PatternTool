package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Listener.Conclusion
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction

internal class TakeEventStream<EventT>(
    initialRemainingCount: Int,
) : StatefulEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT> construct(
            source: EventStream<EventT>,
            count: Int,
        ): TakeEventStream<EventT> = TakeEventStream<EventT>(
            initialRemainingCount = count,
        ).apply {
            momentContext.transaction.enqueueMutation {
                source.listenWeak(
                    target = this@apply,
                    listener = object : TargetingListener<TakeEventStream<EventT>, EventT> {
                        override fun handle(
                            transaction: Transaction,
                            target: TakeEventStream<EventT>,
                            event: EventT,
                        ): Conclusion {
                            val self = target

                            val oldRemainingCount = self.remainingCount

                            if (oldRemainingCount <= 0) {
                                // Abortion failed (?)
                                throw AssertionError("No more remaining events to take")
                            }

                            val newRemainingCount = oldRemainingCount - 1

                            self.remainingCount = newRemainingCount

                            self.notify(
                                transaction = transaction,
                                event = event,
                            )

                            if (newRemainingCount == 0) {
                                self.abort()

                                return Listener.Conclusion.StopListening
                            } else {
                                return Listener.Conclusion.KeepListening
                            }
                        }
                    },
                )
            }
        }
    }

    private var remainingCount: Int = initialRemainingCount
}
