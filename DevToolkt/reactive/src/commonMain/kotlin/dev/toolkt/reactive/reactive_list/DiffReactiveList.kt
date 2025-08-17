package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

// This is a very naive implementation, it could be improved (but there might be no single one obviously preferable
// diff strategy)
class DiffReactiveList<ElementT>(
    private val source: Cell<List<ElementT>>,
) : ActiveReactiveList<ElementT>() {
    override val changes: EventStream<Change<ElementT>> = source.newValues.map { newElements ->
        Change.single(
            update = Change.Update.change(
                indexRange = source.currentValueUnmanaged.indices,
                changedElements = newElements,
            ),
        )
    }

    override val currentElements: List<ElementT>
        get() = source.currentValueUnmanaged
}
