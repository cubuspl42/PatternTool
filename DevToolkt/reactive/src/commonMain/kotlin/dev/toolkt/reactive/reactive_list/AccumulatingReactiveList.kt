package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.event_stream.EventStream

abstract class AccumulatingReactiveList<SourceEventT, ElementT>(
    source: EventStream<SourceEventT>,
    initialContent: List<ElementT>,
) : ActiveReactiveList<ElementT>() {
    private class AppliedListChangeStream<ElementT>(
        changes: EventStream<Change<ElementT>>,
        mutableList: MutableList<ElementT>,
    ) : ProxyEventStream<Change<ElementT>>(
        source = changes,
    ) {
        private val mutableListWeakRef = PlatformWeakReference(mutableList)

        override fun onNotified(event: Change<ElementT>) {
            val mutableList = mutableListWeakRef.get() ?: return
            event.applyTo(mutableList = mutableList)
        }

        init {
            pinWeak(target = mutableList)
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
