package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mergeWith
import dev.toolkt.reactive.effect.MomentContext

class SwitchCell<V>(
    private val nestedCell: Cell<Cell<V>>,
) : ProperCell<V>() {
    override val currentValueUnmanaged: V
        get() = nestedCell.currentValueUnmanaged.currentValueUnmanaged

    override val newValues: EventStream<V> = nestedCell.newValues.map { newInnerCell ->
        newInnerCell.currentValueUnmanaged
    }.mergeWith(
        nestedCell.divertOf { it.newValues },
    )

    context(momentContext: MomentContext) override fun sample(): V = nestedCell.sample().sample()
}
