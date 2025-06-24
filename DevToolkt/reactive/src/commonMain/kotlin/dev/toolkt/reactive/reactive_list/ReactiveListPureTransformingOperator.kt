package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream

abstract class ReactiveListPureTransformingOperator<E, Er>(
    val source: ReactiveList<E>,
) : ReactiveListPureOperator<Er>() {
    override fun buildChanges(): EventStream<ReactiveList.Change<Er>> = source.changes.map(
        transform = this::transformChange,
    )

    override fun getCurrentContent(): List<Er> = transformElements(source.currentElements)

    abstract fun transformElements(
        elements: List<E>,
    ): List<Er>

    abstract fun transformChange(
        change: ReactiveList.Change<E>,
    ): ReactiveList.Change<Er>
}
