package diy.lingerie.frp.vertices.cell

import diy.lingerie.frp.vertices.cell.CellVertex

class MutableCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue,
) {
    fun set(newValue: V) {
        update(newValue)
    }
}

