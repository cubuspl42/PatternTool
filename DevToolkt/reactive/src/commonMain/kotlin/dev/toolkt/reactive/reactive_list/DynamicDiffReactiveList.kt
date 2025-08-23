package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mergeWith

class DynamicDiffReactiveList<ElementT>(
    private val source: Cell<ReactiveList<ElementT>>,
) : ProperReactiveList<ElementT>() {
    override val changes: EventStream<Change<ElementT>> = source.divertOf {
        it.changes
    }.mergeWith(
        source.newValues.mapAt { newReactiveList ->
            // What if `newReactiveList` changes itself _now_? (similar issue to`Cell.switch`)
            Change.single(
                update = Change.Update.change(
                    indexRange = sampleContent().indices,
                    changedElements = newReactiveList.sampleContent(),
                ),
            )
        })

    override val currentElementsUnmanaged: List<ElementT>
        get() = source.currentValueUnmanaged.currentElementsUnmanaged
}
