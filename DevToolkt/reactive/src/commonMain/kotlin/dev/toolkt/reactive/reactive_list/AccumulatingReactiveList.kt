package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream

abstract class AccumulatingReactiveList<SourceEventT, ElementT>(
    source: EventStream<SourceEventT>,
    initialContent: List<ElementT>,
) : ActiveReactiveList<ElementT>() {
    private class AppliedListChangeStream<ElementT>(
        changes: EventStream<Change<ElementT>>,
        private val mutableList: MutableList<ElementT>,
    ) : ProxyEventStream<Change<ElementT>>(
        source = changes,
    ) {
        // TODO: Pin this stream _somehow_ based on a weak observation of `accumulatedElements`
        // Otherwise, `onNotified` won't be called when there are no listeners, which will break the use case when
        // this reactive list is kept for sampling purposes

        override fun onNotified(event: Change<ElementT>) {
            // TODO: Ensure not to store a strong reference that would keep the list alive unnecessarily
            event.applyTo(mutableList)
        }
    }

    private val accumulatedElements: MutableList<ElementT> = initialContent.toMutableList()

    final override val changes: EventStream<Change<ElementT>> = AppliedListChangeStream(
        changes = source.mapNotNull { sourceEvent ->
            accumulate(
                sourceEvent = sourceEvent,
                currentElements = accumulatedElements,
            )
        },
        mutableList = accumulatedElements,
    )

    final override val currentElements: List<ElementT>
        get() = accumulatedElements

    abstract fun accumulate(
        sourceEvent: SourceEventT,
        currentElements: List<ElementT>,
    ): Change<ElementT>?
}
