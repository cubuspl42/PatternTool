package dev.toolkt.reactive.reactive_list

class ReactiveListPureMapOperator<E, Er>(
    source: ActiveReactiveList<E>,
    private val transform: (E) -> Er,
) : ReactiveListPureTransformingOperator<E, Er>(
    source = source,
) {
    override fun transformElements(
        elements: List<E>,
    ): List<Er> = elements.map(transform)

    override fun transformChange(
        change: ReactiveList.Change<E>,
    ): ReactiveList.Change<Er> = change.map(transform)
}
