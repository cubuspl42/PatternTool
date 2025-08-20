package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effective
import dev.toolkt.reactive.effect.Transaction

internal class MapExecutingEventStream<TransformedEventT> private constructor() :
    ManagedEventStream<TransformedEventT>() {

    companion object {
        context(actionContext: ActionContext) fun <EventT, TransformedEventT> construct(
            source: EventStream<EventT>,
            transform: context(ActionContext) (EventT) -> TransformedEventT,
        ): Effective<MapExecutingEventStream<TransformedEventT>> = MapExecutingEventStream<TransformedEventT>().let { self ->
            Effective(
                result = self,
                handle = source.watch(
                    listener = object : UnconditionalListener<EventT>() {
                        override fun handleUnconditionally(
                            transaction: Transaction,
                            event: EventT,
                        ) {
                            val transformedEvent = with(transaction) {
                                transform(event)
                            }

                            self.notify(
                                transaction = transaction,
                                event = transformedEvent,
                            )
                        }
                    },
                ),
            )
        }
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onAborted() {
    }
}
