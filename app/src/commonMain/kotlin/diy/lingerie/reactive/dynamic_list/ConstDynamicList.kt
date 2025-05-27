package diy.lingerie.reactive.dynamic_list

import diy.lingerie.reactive.event_stream.EventStream

data class ConstDynamicList<out E>(
    private val constElements: List<E>,
) : DynamicList<E>() {
    override val currentElements: List<E>
        get() = constElements

    override val changes: EventStream<Change<E>> = EventStream.Never

    override fun <Er> map(
        transform: (E) -> Er,
    ): DynamicList<Er> = ConstDynamicList(
        constElements = constElements.map(transform),
    )

    override fun <T : Any> pipe(
        target: T,
        mutableList: MutableList<in E>,
    ) {
        copyNow(mutableList = mutableList)
    }
}
