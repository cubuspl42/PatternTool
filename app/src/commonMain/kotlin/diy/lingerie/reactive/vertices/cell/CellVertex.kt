package diy.lingerie.reactive.vertices.cell

import diy.lingerie.reactive.cell.Cell
import diy.lingerie.reactive.vertices.Vertex

abstract class CellVertex<V>(
    initialValue: V,
) : Vertex<Cell.Change<V>>() {
    private var mutableValue: V = initialValue

    val currentValue: V
        get() = mutableValue

    protected fun update(newValue: V) {
        val oldValue = mutableValue

        mutableValue = newValue

        notify(
            Cell.Change(
                oldValue = oldValue,
                newValue = newValue,
            )
        )
    }
}
