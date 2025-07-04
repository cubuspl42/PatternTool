package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.pinWeak
import dev.toolkt.reactive.reactive_list.ProxyEventStream

abstract class CachingCell<V>(
    initialValue: V,
    newValues: EventStream<V>,
) : ProperCell<V>() {
    private var cachedValue: V = initialValue

    final override val newValues: EventStream<V> = object : ProxyEventStream<V>(
        newValues,
    ) {
        override fun onNotified(event: V) {
            cachedValue = event
        }
    }

    override val currentValue: V
        get() = cachedValue

    protected fun init() {
        newValues.pinWeak(
            target = this,
        )
    }
}
