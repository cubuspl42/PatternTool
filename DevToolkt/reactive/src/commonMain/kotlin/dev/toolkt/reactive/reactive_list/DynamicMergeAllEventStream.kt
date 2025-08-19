package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.removeRange
import dev.toolkt.core.iterable.subList
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.listenInDependent

class DynamicMergeAllEventStream<EventT>(
    private val eventStreams: ReactiveList<EventStream<EventT>>,
) : DependentEventStream<EventT>() {
    override fun observe(): Subscription {
        val mutableSubscriptions = eventStreams.currentElementsUnmanaged.mapTo(mutableListOf(), ::forwardFrom)

        val changeSubscription = eventStreams.changes.listenInDependent(
            dependent = this@DynamicMergeAllEventStream,
        ) { change: ReactiveList.Change<EventStream<EventT>> ->
            val update = change.update
            val indexRange = update.indexRange

            val newSubscriptions = update.updatedElements.map(::forwardFrom)

            mutableSubscriptions.updateRange(
                indexRange = indexRange,
                dispose = Subscription::cancel,
                newElements = newSubscriptions,
            )
        }

        return object : Subscription {
            override fun cancel() {
                changeSubscription.cancel()

                mutableSubscriptions.forEach(Subscription::cancel)
            }
        }
    }
}

private fun <E> MutableList<E>.updateRange(
    indexRange: IntRange,
    dispose: (E) -> Unit,
    newElements: List<E>,
) {
    subList(
        indexRange = indexRange,
    ).forEach { element ->
        dispose(element)
    }

    removeRange(
        indexRange = indexRange,
    )

    addAll(
        index = indexRange.first,
        elements = newElements,
    )
}
