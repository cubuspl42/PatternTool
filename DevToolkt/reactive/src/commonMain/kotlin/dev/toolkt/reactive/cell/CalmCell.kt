package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class CalmCell<ValueT>(
    private val source: Cell<ValueT>,
) : ProperCell<ValueT>() {
    override val newValues: EventStream<ValueT> = source.newValues.filter {
        it != currentValue
    }

    override val currentValue: ValueT
        get() = source.currentValue
}
