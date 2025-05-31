package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventHandler
import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.subscribeWeak

abstract class DependentReactiveList<E>(
    initialContent: List<E>,
) : ActiveReactiveList<E>() {
    internal val cachedContent = initialContent.toMutableList()

    final override val currentElements: List<E>
        get() = cachedContent.toList()

    protected fun init() {
        changes.subscribeWeak(
            eventHandler = object : EventHandler<Change<E>> {
                override fun handleEvent(
                    source: EventSource<Change<E>>, event: Change<E>
                ) {
                    val change = event

                    change.applyTo(
                        mutableList = cachedContent,
                    )
                }

                override fun handleStop(source: EventSource<Change<E>>) {
                }
            },
        )
    }
}
