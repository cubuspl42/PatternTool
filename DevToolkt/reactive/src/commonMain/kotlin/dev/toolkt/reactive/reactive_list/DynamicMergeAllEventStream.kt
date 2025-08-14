package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.removeRange
import dev.toolkt.core.iterable.subList
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.ProperEventStream
import dev.toolkt.reactive.event_stream.listenInDependent

class DynamicMergeAllEventStream<EventT>(
    private val eventStreams: ReactiveList<EventStream<EventT>>,
) : DependentEventStream<EventT>() {
    override fun observe(): Subscription = eventStreams.maintain(
        dependent = this@DynamicMergeAllEventStream,
        subscribe = {
            it.listen { event ->
                notify(event)
            }
        },
    )
}

private fun <ElementT> ReactiveList<ElementT>.maintain(
    dependent: ProperEventStream<*>,
    subscribe: (ElementT) -> Subscription,
): Subscription {
    val mutableSubscriptions = currentElements.mapTo(mutableListOf(), subscribe)

    val changeSubscription = changes.listenInDependent(
        dependent = dependent,
    ) { change: ReactiveList.Change<ElementT> ->
        val update = change.update
        val indexRange = update.indexRange

        val newSubscriptions = update.updatedElements.map(subscribe)

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
