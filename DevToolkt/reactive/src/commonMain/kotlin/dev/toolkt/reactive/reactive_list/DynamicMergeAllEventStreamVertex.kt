package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.updateRange
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.vertex.PassiveEventStreamVertex

internal class DynamicMergeAllEventStreamVertex<EventT>(
    private val eventStreams: ReactiveList<EventStream<EventT>>,
) : PassiveEventStreamVertex<EventT>() {

    override fun observe(): Subscription = object : Subscription {
        private val mutableInnerSubscriptions =
            eventStreams.currentElementsUnmanaged.mapTo(mutableListOf()) { initialEventStream ->
                subscribeToInner(eventStream = initialEventStream)
            }

        private val outerSubscription = eventStreams.changes.listen(
            object : UnconditionalListener<ReactiveList.Change<EventStream<EventT>>>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: ReactiveList.Change<EventStream<EventT>>,
                ) {
                    val newChange = event

                    val update = newChange.update
                    val indexRange = update.indexRange

                    val newSubscriptions = update.updatedElements.map { newEventStream ->
                        subscribeToInner(eventStream = newEventStream)
                    }

                    mutableInnerSubscriptions.updateRange(
                        indexRange = indexRange,
                        dispose = Subscription::cancel,
                        newElements = newSubscriptions,
                    )
                }
            },
        )

        override fun cancel() {
            outerSubscription.cancel()

            mutableInnerSubscriptions.forEach(Subscription::cancel)
        }
    }

    private fun subscribeToInner(
        eventStream: EventStream<EventT>,
    ): Subscription = eventStream.listen(
        listener = object : UnconditionalListener<EventT>() {
            override fun handleUnconditionally(
                transaction: Transaction,
                event: EventT,
            ) {
                notify(
                    transaction = transaction,
                    event = event,
                )
            }
        },
    )
}
