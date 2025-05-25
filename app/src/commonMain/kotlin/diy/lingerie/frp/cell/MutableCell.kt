package diy.lingerie.frp.cell

import diy.lingerie.frp.vertices.cell.MutableCellVertex

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
