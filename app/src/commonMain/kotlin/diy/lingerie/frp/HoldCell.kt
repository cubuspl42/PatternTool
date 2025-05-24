package diy.lingerie.frp

internal class HoldCell<V>(
    values: EventStream<V>,
    initialValue: V,
) : ChangingCell<V>(
    initialValue = initialValue,
) {
    init {
        values.subscribeFullyBound(
            target = this,
            listener = object : Listener<V> {
                override fun handle(value: V) {
                    update(value)
                }
            },
        )
    }
}
