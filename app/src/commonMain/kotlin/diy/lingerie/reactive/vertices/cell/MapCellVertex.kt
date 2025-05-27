package diy.lingerie.reactive.vertices.cell

import diy.lingerie.reactive.Listener
import diy.lingerie.reactive.cell.Cell

internal class MapCellVertex<V, Vr>(
    private val source: CellVertex<V>,
    private val transform: (V) -> Vr,
) : DependentCellVertex<Vr>(
    initialValue = transform(source.currentValue),
) {
    override val kind: String = "MapC"

    override fun buildHybridSubscription() = source.subscribeHybrid(
         listener = object : Listener<Cell.Change<V>> {
             override fun handle(change: Cell.Change<V>) {
                 val newValue = change.newValue

                 update(transform(newValue))
             }
         },
     )

    init {
        init()
    }
}
