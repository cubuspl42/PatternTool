package diy.lingerie.frp

class MutableCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue,
) {
    fun set(newValue: V) {
        update(newValue)
    }
}

