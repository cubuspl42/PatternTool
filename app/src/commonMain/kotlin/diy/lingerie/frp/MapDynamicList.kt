package diy.lingerie.frp

class MapDynamicListVertex<E, Er>(
    private val source: DynamicListVertex<E>,
    private val transform: (E) -> Er,
) : DependentDynamicListVertex<Er>(
    initialElements = source.currentElements.map(transform),
) {
    override fun buildInitialSubscription(): Subscription = source.subscribe(
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
