package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.effect.MomentContext

class CalmCell<ValueT>(
    private val source: Cell<ValueT>,
) : ProperCell<ValueT>() {
    override val newValues: EventStream<ValueT> = source.newValues.filterAt {
        it != sample()
    }

    context(momentContext: MomentContext)
    override fun sample(): ValueT = source.sample()

    override val currentValueUnmanaged: ValueT
        get() = source.currentValueUnmanaged
}
