package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext

class CellSlot<ValueT> private constructor(
    private val mutableCell: MutableCell<Cell<ValueT>>,
    private val switchedCell: Cell<ValueT>,
) : ProperCell<ValueT>() {
    companion object {
        context(actionContext: ActionContext) fun <ValueT> create(
            initialValue: ValueT,
        ): CellSlot<ValueT> {
            val mutableCell = MutableCell.create(
                Cell.of(initialValue),
            )

            return CellSlot(
                mutableCell = mutableCell,
                switchedCell = mutableCell.switch(),
            )
        }
    }

    override val newValues: EventStream<ValueT>
        get() = switchedCell.newValues

    context(momentContext: MomentContext)
    override fun sample(): ValueT = switchedCell.sample()

    override val currentValueUnmanaged: ValueT
        get() = switchedCell.currentValueUnmanaged

    context(actionContext: ActionContext) fun bind(
        cell: Cell<ValueT>,
    ) {
        mutableCell.set(cell)
    }
}
