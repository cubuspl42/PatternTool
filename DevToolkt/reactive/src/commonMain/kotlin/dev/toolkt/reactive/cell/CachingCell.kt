package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventHandler
import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribeWeak

abstract class CachingCell<V>(
    initialValue: V,
    final override val newValues: EventStream<V>,
) : ProperCell<V>(), EventHandler<V> {
    private var cachedValue: V = initialValue

    override val currentValue: V
        get() = cachedValue

    protected fun init() {
        newValues.subscribeWeak(eventHandler = this)
    }

    final override fun handleEvent(
        source: EventSource<V>,
        event: V,
    ) {
        cachedValue = event
    }

    override fun handleStop(
        source: EventSource<V>,
    ) {
    }
}
