package diy.lingerie.frp

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
