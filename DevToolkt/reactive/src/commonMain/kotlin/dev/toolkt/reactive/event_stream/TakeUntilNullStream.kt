package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Listener.Conclusion
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction

internal class TakeUntilNullStream<EventT : Any> : StatefulEventStream<EventT>() {
    companion object {
        context(momentContext: MomentContext) fun <EventT : Any> construct(
            source: EventStream<EventT?>,
        ): TakeUntilNullStream<EventT> = TakeUntilNullStream<EventT>().apply {
            momentContext.transaction.enqueueMutation {
                source.listenWeak(
                    target = this@apply,
                    listener = object : TargetingListener<TakeUntilNullStream<EventT>, EventT?> {
                        override fun handle(
                            transaction: Transaction,
                            target: TakeUntilNullStream<EventT>,
                            event: EventT?,
                        ): Conclusion {
                            val self = target

                            when (event) {
                                null -> {
                                    self.abort()

                                    return Listener.Conclusion.StopListening
                                }

                                else -> {
                                    self.notify(
                                        transaction = transaction,
                                        event = event,
                                    )

                                    return Listener.Conclusion.KeepListening
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}
