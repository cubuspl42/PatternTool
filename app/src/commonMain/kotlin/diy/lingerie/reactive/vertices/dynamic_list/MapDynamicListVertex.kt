package diy.lingerie.reactive.vertices.dynamic_list

import diy.lingerie.reactive.dynamic_list.DynamicList
import diy.lingerie.reactive.Listener

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
