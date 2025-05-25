package diy.lingerie.frp.vertices.dynamic_list

import diy.lingerie.frp.dynamic_list.DynamicList
import diy.lingerie.frp.Listener
import diy.lingerie.frp.Subscription

class MapDynamicListVertex<E, Er>(
    private val source: DynamicListVertex<E>,
    private val transform: (E) -> Er,
) : DependentDynamicListVertex<Er>(
    initialElements = source.currentElements.map(transform),
) {
    override val kind: String = "MapL"

    override fun buildHybridSubscription() = source.subscribeHybrid(
        listener = object : Listener<DynamicList.Change<E>> {
            override fun handle(change: DynamicList.Change<E>) {
                update(
                    change = DynamicList.Change(
                        updates = change.updates.map { update ->
                            DynamicList.Change.Update(
                                indexRange = update.indexRange,
                                updatedElements = update.updatedElements.map(transform),
                            )
                        }.toSet(),
                    ),
                )
            }
        },
    )

    init {
        init()
    }
}
