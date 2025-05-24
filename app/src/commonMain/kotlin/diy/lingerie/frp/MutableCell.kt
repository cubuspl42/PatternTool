package diy.lingerie.frp

class MutableCell<V>(
    initialValue: V,
) : ChangingCell<V>(
    initialValue,
) {
    fun set(newValue: V) {
        update(newValue)
    }
}
