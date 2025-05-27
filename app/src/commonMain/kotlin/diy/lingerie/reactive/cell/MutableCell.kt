package diy.lingerie.reactive.cell

import diy.lingerie.reactive.vertices.cell.MutableCellVertex

class MutableCell<V>(
    initialValue: V,
) : ActiveCell<V>() {
    override val vertex = MutableCellVertex(
        initialValue = initialValue,
    )

    fun set(newValue: V) {
        vertex.set(newValue)
    }
}
