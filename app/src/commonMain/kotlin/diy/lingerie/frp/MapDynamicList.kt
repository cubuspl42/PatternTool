package diy.lingerie.frp

class MapDynamicList<E, Er>(
    private val source: DynamicList<E>,
    private val transform: (E) -> Er,
) : DynamicList<Er>() {
    override val currentElements: List<Er>
        get() = source.currentElements.map(transform)

    override val changes: EventStream<Change<Er>> = source.changes.map { change ->
        Change(
            updates = change.updates.map { update ->
                Change.Update(
                    indexRange = update.indexRange,
                    updatedElements = update.updatedElements.map(transform),
                )
            }.toSet(),
        )
    }
}
