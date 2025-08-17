package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.managed_io.ActionContext

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

    override val currentValue: ValueT
        get() = switchedCell.currentValue

    context(actionContext: ActionContext) fun bind(
        cell: Cell<ValueT>,
    ) {
        mutableCell.set(cell)
    }
}
