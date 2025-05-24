package diy.lingerie.frp

external class WeakRef<T>(value: T) {
    fun deref(): T?
}
