package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.ProperCell
import dev.toolkt.reactive.managed_io.ReactionContext

// TODO: Add tests
class CellSlot<ValueT> private constructor() : ProperCell<ValueT>() {
    companion object {
        context(reactionContext: ReactionContext) fun <ValueT> create(): CellSlot<ValueT> {
            TODO()
        }
    }

    override val newValues: EventStream<ValueT>
        get() = TODO("Not yet implemented")

    override val currentValue: ValueT
        get() = TODO("Not yet implemented")

    context(reactionContext: ReactionContext) fun bind(
        cell: Cell<ValueT>,
    ) {
        TODO()
    }
}
