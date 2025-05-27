package diy.lingerie.reactive.vertices.dynamic_list

import diy.lingerie.reactive.reactive_list.ReactiveList
import diy.lingerie.reactive.Listener

class MapDynamicListVertex<E, Er>(
    private val source: DynamicListVertex<E>,
    private val transform: (E) -> Er,
) : DependentDynamicListVertex<Er>(
    initialElements = source.currentElements.map(transform),
) {
    override val kind: String = "MapL"

    override fun buildHybridSubscription() = source.subscribeHybrid(
        listener = object : Listener<ReactiveList.Change<E>> {
            override fun handle(change: ReactiveList.Change<E>) {
                update(
                    change = ReactiveList.Change(
                        updates = change.updates.map { update ->
                            ReactiveList.Change.Update(
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
