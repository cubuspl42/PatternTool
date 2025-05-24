package diy.lingerie.frp

data class ConstDynamicList<out E>(
    private val constElements: List<E>,
) : DynamicList<E>() {
    override val currentElements: List<E>
        get() = constElements

    override val changes: EventStream<Change<E>> = EventStream.Never
}
