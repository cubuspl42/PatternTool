package diy.lingerie.frp.vertices.cell

class MutableCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue,
) {
    override val kind: String = "MutableC"

    fun set(newValue: V) {
        update(newValue)
    }
}
