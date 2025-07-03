package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

class MutableCell<V>(
    initialValue: V,
) : ProperCell<V>() {
    private val newValueEmitter = EventEmitter<V>()

    private var mutableValue: V = initialValue

    val hasListeners: Boolean
        get() = newValueEmitter.hasListeners

    override val newValues: EventStream<V>
        get() = newValueEmitter

    override val currentValue: V
        get() = mutableValue

    fun set(
        newValue: V,
    ) {
        newValueEmitter.emit(newValue)
        mutableValue = newValue
    }
}
