package diy.lingerie.frp

internal class MapCell<V, Vr>(
    private val source: Cell<V>,
    private val transform: (V) -> Vr,
) : ChangingCell<Vr>(
    initialValue = transform(source.currentValue),
) {
    init {
        source.newValues.subscribeFullyBound(
            target = this,
            listener = object : Listener<V> {
                override fun handle(event: V) {
                    update(transform(event))
                }
            },
        )
    }
}
