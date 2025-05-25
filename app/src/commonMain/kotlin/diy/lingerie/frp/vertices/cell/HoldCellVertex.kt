package diy.lingerie.frp.vertices.cell

import diy.lingerie.frp.Listener
import diy.lingerie.frp.Subscription
import diy.lingerie.frp.vertices.Vertex

internal class HoldCellVertex<V>(
    private val values: Vertex<V>,
    initialValue: V,
) : DependentCellVertex<V>(
    initialValue = initialValue,
) {
    override fun buildHybridSubscription() = values.subscribeHybrid(
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
