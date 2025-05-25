package diy.lingerie.frp.vertices.cell

class MutableCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue,
) {
    fun set(newValue: V) {
        update(newValue)
    }
}
