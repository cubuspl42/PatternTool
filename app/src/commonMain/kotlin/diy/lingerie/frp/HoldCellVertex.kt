package diy.lingerie.frp

internal class HoldCellVertex<V>(
    private val values: Vertex<V>,
    initialValue: V,
) : DependentCellVertex<V>(
    initialValue = initialValue,
) {
    override fun buildInitialSubscription(): Subscription = values.subscribe(
        listener = object : Listener<V> {
            override fun handle(value: V) {
                update(value)
            }
        },
    )

    init {
        init()
    }
}
