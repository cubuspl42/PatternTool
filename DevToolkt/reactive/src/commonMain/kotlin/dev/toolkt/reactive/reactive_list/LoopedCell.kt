package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.ProperCell
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.LoopedEventStream
import dev.toolkt.reactive.effect.MomentContext

class LoopedCell<V>(
    private val placeholderValue: V,
) : ProperCell<V>() {
    private var loopedCell: Cell<V>? = null

    private val newValuesLooped = LoopedEventStream<V>()

    override val newValues: EventStream<V>
        get() = newValuesLooped

    context(momentContext: MomentContext) override fun sample(): V = currentValueUnmanaged

    override val currentValueUnmanaged: V
        get() = this.loopedCell?.currentValueUnmanaged ?: placeholderValue

    fun loop(
        cell: Cell<V>,
    ) {
        if (loopedCell != null) {
            throw IllegalStateException("The reactive list is already looped")
        }

        loopedCell = cell

        newValuesLooped.loop(
            eventStream = cell.newValues,
            initialEvent = cell.currentValueUnmanaged,
        )
    }
}
