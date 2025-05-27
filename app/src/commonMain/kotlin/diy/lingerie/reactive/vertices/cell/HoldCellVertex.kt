package diy.lingerie.reactive.vertices.cell

import diy.lingerie.reactive.Listener
import diy.lingerie.reactive.vertices.Vertex

internal class HoldCellVertex<V>(
    private val values: Vertex<V>,
    initialValue: V,
) : DependentCellVertex<V>(
    initialValue = initialValue,
) {
    override val kind: String = "Hold"

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
